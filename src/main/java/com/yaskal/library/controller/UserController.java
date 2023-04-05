package com.yaskal.library.controller;

import com.yaskal.library.exception.UserAlreadyExistsException;
import com.yaskal.library.model.UserDto;
import com.yaskal.library.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
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
        return new ModelAndView("login");
    }


}
