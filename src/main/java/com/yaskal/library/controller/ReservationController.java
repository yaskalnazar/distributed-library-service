package com.yaskal.library.controller;

import com.yaskal.library.model.BookDto;
import com.yaskal.library.model.ReservationDto;
import com.yaskal.library.service.BookService;
import com.yaskal.library.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private BookService bookService;

    @PostMapping
    public ModelAndView addReservation(@ModelAttribute ReservationDto reservationDto) {
        ModelAndView mav = new ModelAndView("main");
        try {
            ReservationDto newReservation = reservationService.addReservation(reservationDto);
            mav.addObject("message", "The book request has been sent.");
        } catch (Exception e) {
            mav.addObject("errorMessage", e.getMessage());
        }
        return mav;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        ReservationDto reservationDto = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationDto);
    }

    // Add more endpoints as needed
}

