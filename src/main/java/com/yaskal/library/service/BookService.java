package com.yaskal.library.service;

import com.yaskal.library.exception.ResourceNotFoundException;
import com.yaskal.library.model.Book;
import com.yaskal.library.model.BookDto;
import com.yaskal.library.repository.BookRepository;
import com.yaskal.library.mapping.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BookMapper bookMapper;

    public List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::toDto).collect(Collectors.toList());
    }


    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        return bookMapper.toDto(book);
    }


    public BookDto createBook(BookDto bookDto, Principal principal) {
        Long userId = userService.getUserByName(principal.getName()).getId();
        bookDto.setContributorId(userId);
        Book book = bookMapper.toEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }


    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        Book updatedBook = bookMapper.toEntity(bookDto);
        updatedBook.setId(book.getId());
        Book savedBook = bookRepository.save(updatedBook);
        return bookMapper.toDto(savedBook);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        bookRepository.delete(book);
    }
}
