package com.yaskal.library.controller;

import com.yaskal.library.exception.UserAlreadyExistsException;
import com.yaskal.library.model.BookDto;
import com.yaskal.library.model.User;
import com.yaskal.library.model.UserDto;
import com.yaskal.library.service.BookService;
import com.yaskal.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.yaskal.library.controller.UserController.DEFAULT_PAGE_SIZE;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final long USER_ID = 1L;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("user", instanceOf(UserDto.class)));
    }

    @Test
    void testProcessRegistration_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("testUser");
        userDto.setPassword("password");

        when(userService.registerUser(userDto)).thenReturn(new UserDto());

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", userDto.getName())
                        .param("password", userDto.getPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    void testProcessRegistration_UserAlreadyExists() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("testUser");
        userDto.setPassword("password");

        when(userService.registerUser(userDto)).thenThrow(new UserAlreadyExistsException("User already exists"));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", userDto.getName())
                        .param("password", userDto.getPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("errorMessage", "User already exists"))
                .andExpect(model().attribute("user", userDto));
    }

    @Test
    void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginPage"));
    }

    @Test
    void testUserProfile() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(USER_ID);

        List<BookDto> contributedBooks = new ArrayList<>();
        BookDto bookDto1 = new BookDto();
        bookDto1.setId(1L);
        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);
        contributedBooks.add(bookDto1);
        contributedBooks.add(bookDto2);

        List<BookDto> borrowedBooks = new ArrayList<>();
        BookDto bookDto3 = new BookDto();
        bookDto3.setId(3L);
        BookDto bookDto4 = new BookDto();
        bookDto4.setId(4L);
        borrowedBooks.add(bookDto3);
        borrowedBooks.add(bookDto4);

        when(userService.getUserById(USER_ID)).thenReturn(userDto);
        when(bookService.findByContributor(USER_ID)).thenReturn(contributedBooks);
        when(bookService.findByCurrentKeeper(USER_ID)).thenReturn(borrowedBooks);

        mockMvc.perform(get("/api/v1/users/" + USER_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("userProfile"))
                .andExpect(model().attribute("user", userDto))
                .andExpect(model().attribute("contributedBooks", contributedBooks))
                .andExpect(model().attribute("borrowedBooks", borrowedBooks));
    }

    @Test
    void testListUsers() throws Exception {
        // Create a mock user page with some dummy data
        List<User> userList = new ArrayList<>();
        userList.add(new User(1L, "testUser1", "testUser1@example.com", "1234567890", "New York", "123 Main St", "password1"));
        userList.add(new User(2L, "testUser2", "testUser2@example.com", "1234567891", "London", "456 High St", "password2"));
        userList.add(new User(3L, "testUser3", "testUser3@example.com", "1234567892", "Sydney", "789 Market St", "password3"));
        userList.add(new User(4L, "testUser4", "testUser4@example.com", "1234567892", "Sydney", "789 Market St", "password4"));

        Page<User> userPage = new PageImpl<>(userList, PageRequest.of(0, DEFAULT_PAGE_SIZE), userList.size());

        // Mock the userService to return the mock user page
        when(userService.getUsersPage(anyInt(), anyInt())).thenReturn(userPage);
        // Perform the GET request to /api/v1/users
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("usersList"))
                .andExpect(model().attributeExists("userPage"))
                .andExpect(model().attribute("userPage", equalTo(userPage)))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attribute("currentPage", equalTo(userPage.getNumber())))
                .andExpect(model().attributeExists("pageNumbers"))
                .andExpect(model().attribute("pageNumbers", hasSize(userPage.getTotalPages())))
                .andExpect(model().attribute("pageNumbers", contains(1, 2)));

        // Verify that the userService method was called with the correct parameters
        verify(userService).getUsersPage(1, DEFAULT_PAGE_SIZE);
    }

}