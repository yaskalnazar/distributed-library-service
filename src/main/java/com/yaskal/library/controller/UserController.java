package com.yaskal.library.controller;

import com.yaskal.library.exception.UserAlreadyExistsException;
import com.yaskal.library.model.BookDto;
import com.yaskal.library.model.User;
import com.yaskal.library.model.UserDto;
import com.yaskal.library.service.BookService;
import com.yaskal.library.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@Slf4j
public class UserController {

    public static final int DEFAULT_PAGE_SIZE = 3;
    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public ModelAndView showRegistrationForm() {
        ModelAndView mav = new ModelAndView("registration");
        mav.addObject("user", new UserDto());
        return mav;
    }

    @PostMapping("/register")
    public ModelAndView processRegistration(@ModelAttribute UserDto user, Model model) {
        ModelAndView mav = new ModelAndView();
        try {
            userService.registerUser(user);
            mav.setViewName("login");
        } catch (UserAlreadyExistsException e) {
            mav.addObject("errorMessage", e.getMessage());
            mav.addObject("user", user);
            mav.setViewName("registration");
        }
        return mav;
    }

    @GetMapping("/login")
    public ModelAndView showLoginForm() {
        return new ModelAndView("loginPage");
    }

    @GetMapping("/api/v1/users/{id}")
    public ModelAndView userProfile(@PathVariable("id") Long id) {
        ModelAndView mav = new ModelAndView("userProfile");

        UserDto user = userService.getUserById(id);

        List<BookDto> contributedBooks = bookService.findByContributor(id);
        List<BookDto> borrowedBooks = bookService.findByCurrentKeeper(id);

        mav.addObject("user", user);
        mav.addObject("contributedBooks", contributedBooks);
        mav.addObject("borrowedBooks", borrowedBooks);
        return mav;
    }

    @GetMapping("/api/v1/users")
    public ModelAndView listUsers(@RequestParam(value = "page", defaultValue = "1") int page) {
        Page<User> userPage = userService.getUsersPage(page, DEFAULT_PAGE_SIZE);
        ModelAndView mav = new ModelAndView("usersList");
        mav.addObject("userPage", userPage);
        mav.addObject("currentPage", userPage.getNumber());
        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            mav.addObject("pageNumbers", pageNumbers);
        }
        return mav;
    }


}
