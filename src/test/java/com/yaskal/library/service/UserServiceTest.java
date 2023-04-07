package com.yaskal.library.service;

import com.yaskal.library.exception.ResourceNotFoundException;
import com.yaskal.library.exception.UserAlreadyExistsException;
import com.yaskal.library.mapping.UserMapper;
import com.yaskal.library.model.User;
import com.yaskal.library.model.UserDto;
import com.yaskal.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;
    private List<User> users;
    private List<UserDto> userDtos;
    private Page<User> userPage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(1L, "test", "test@example.com", "123456789", "test city", "test address", "password");
        userDto = new UserDto(1L, "test", "test@example.com", "123456789", "test city", "test address", "password");

        users = Arrays.asList(user);
        userDtos = Arrays.asList(userDto);

        userPage = new PageImpl<>(users);
    }

    @Test
    void testLoadUserByUsername_Success() {
        when(userRepository.findByName(user.getName())).thenReturn(user);
        UserDetails userDetails = userService.loadUserByUsername(user.getName());
        assertEquals(user.getName(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByName(user.getName())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(user.getName()));
    }

    @Test
    void testGetUserByName_Success() {
        when(userRepository.findByName(user.getName())).thenReturn(user);
        User result = userService.getUserByName(user.getName());
        assertEquals(user, result);
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        UserDto result = userService.getUserById(user.getId());
        assertEquals(userDto, result);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    void testRegisterUser_Success() throws UserAlreadyExistsException {
        when(userRepository.existsByName(userDto.getName())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn(user.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
        UserDto result = userService.registerUser(userDto);
        assertEquals(userDto, result);
    }

    @Test
    void testRegisterUser_WithExistingName() {
        UserDto userDto = new UserDto();
        userDto.setName("testuser");
        userDto.setEmail("test@test.com");
        userDto.setPassword("password");

        when(userRepository.existsByName(userDto.getName())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(userDto));

        verify(userRepository, times(1)).existsByName(userDto.getName());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testGetUsersPage() {
        int pageNumber = 1;
        int pageSize = 10;

        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());

        Page<User> userPage = new PageImpl<>(users);

        when(userRepository.findAll(PageRequest.of(pageNumber - 1 , pageSize, Sort.by("name").ascending()))).thenReturn(userPage);

        Page<User> actualResult = userService.getUsersPage(pageNumber, pageSize);

        assertEquals(userPage, actualResult);

        verify(userRepository, times(1)).findAll(PageRequest.of(pageNumber - 1 , pageSize, Sort.by("name").ascending()));
        verifyNoMoreInteractions(userRepository);
    }

}
