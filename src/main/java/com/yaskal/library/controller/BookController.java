package com.yaskal.library.controller;

import com.yaskal.library.model.BookDto;
import com.yaskal.library.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/books")
public class BookController {

    @Autowired
    private BookService bookService;

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

    @GetMapping("/dashboard")
    public ModelAndView showDashboard() {
        List<BookDto> allBooks = bookService.getAllBooks();
        ModelAndView mav = new ModelAndView("dashboard");
        mav.addObject("allBooks", allBooks);
        return mav;
    }

    @PostMapping("/dashboard")
    public ModelAndView filterBooks(@RequestParam(value = "title", required = false) String title,
                                    @RequestParam(value = "author", required = false) String author,
                                    @RequestParam(value = "genres", required = false) String genres,
                                    @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                    @RequestParam(value = "endDate", required = false) LocalDate endDate) {
        List<BookDto> filteredBooks = bookService.findByFilters(title, author, genres, startDate, endDate);
        ModelAndView mav = new ModelAndView("dashboard");
        mav.addObject("allBooks", filteredBooks);
        mav.addObject("title", title);
        mav.addObject("author", author);
        mav.addObject("genres", genres);
        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        return mav;
    }

    @GetMapping
    public ModelAndView getAllBooks() {
        ModelAndView mav = new ModelAndView("books");
        List<BookDto> books = bookService.getAllBooks();
        mav.addObject("books", books);
        return mav;
    }
}

