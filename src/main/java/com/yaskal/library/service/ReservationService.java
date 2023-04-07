package com.yaskal.library.service;

import com.yaskal.library.exception.BookNotAvailableException;
import com.yaskal.library.exception.ResourceNotFoundException;
import com.yaskal.library.mapping.ReservationMapper;
import com.yaskal.library.model.Book;
import com.yaskal.library.model.Reservation;
import com.yaskal.library.model.ReservationDto;
import com.yaskal.library.model.User;
import com.yaskal.library.repository.BookRepository;
import com.yaskal.library.repository.ReservationRepository;
import com.yaskal.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationMapper mapper;


    public ReservationDto addReservation(ReservationDto reservationDto) {
        Book book = bookRepository.findById(reservationDto.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        User user = userRepository.findById(reservationDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if the book is available
        if (!book.isAvailable()) {
            throw new BookNotAvailableException("Book is not available");
        }

        // Create the reservation
        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUser(user);
        reservation.setStatus(reservationDto.getStatus());
        reservation.setReservationDate(LocalDateTime.now());

        // Update the book availability status
        book.setCurrentKeeper(user);
        bookRepository.save(book);

        // Save the reservation
        Reservation savedReservation = reservationRepository.save(reservation);

        return mapper.toDto(savedReservation);
    }

    public ReservationDto getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        return mapper.toDto(reservation);
    }

    // Add more methods as needed
}
