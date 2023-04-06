package com.yaskal.library.controller;

import com.yaskal.library.model.BookDto;
import com.yaskal.library.model.User;
import com.yaskal.library.repository.UserRepository;
import com.yaskal.library.service.BookService;
import com.yaskal.library.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/books")
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/main")
    public ModelAndView getBooks(Principal principal) {
        ModelAndView mav = new ModelAndView("main");
        String username = principal.getName();
        mav.addObject("username", username);
        return mav;
    }

    @GetMapping("/add")
    public ModelAndView getAddBookForm() {
        ModelAndView mav = new ModelAndView("addBook");
        mav.addObject("bookDto", new BookDto());
        return mav;
    }

    @PostMapping
    public ModelAndView createBook(@ModelAttribute BookDto bookDto, Principal principal) {
        ModelAndView mav = new ModelAndView("addBook");
        bookService.createBook(bookDto, principal);
        mav.addObject("message", "Book added successfully.");
        return mav;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView showEditBookForm(@PathVariable Long id) {
        BookDto bookDto = bookService.getBookById(id);
        ModelAndView mav = new ModelAndView("editBook");
        mav.addObject("book", bookDto);
        return mav;
    }

    @PostMapping("/{id}/edit")
    public ModelAndView editBook(@PathVariable Long id, @ModelAttribute("book") BookDto bookDto) {
        ModelAndView mav = new ModelAndView("editBook");
        BookDto updateBook = bookService.updateBook(id, bookDto);
        mav.addObject("message", "Book edited successfully.");
        mav.addObject("book", updateBook);
        return mav;
    }


    @GetMapping
    public ModelAndView getAllBooks() {
        ModelAndView mav = new ModelAndView("books");
        List<BookDto> books = bookService.getAllBooks();
        mav.addObject("books", books);
        return mav;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        BookDto book = bookService.getBookById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
        BookDto updatedBook = bookService.updateBook(id, bookDto);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

