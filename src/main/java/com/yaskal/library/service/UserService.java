package com.yaskal.library.service;

import com.yaskal.library.exception.ResourceNotFoundException;
import com.yaskal.library.exception.UserAlreadyExistsException;
import com.yaskal.library.mapping.UserMapper;
import com.yaskal.library.model.User;
import com.yaskal.library.model.UserDto;
import com.yaskal.library.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to authenticate user with username: {}", username);

        User user = userRepository.findByName(username);
        if (user == null) {
            log.warn("User with username: {} not found", username);
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        log.info("User with username: {} found. Checking credentials.", username);


        return new org.springframework.security.core.userdetails.User(user.getName(),
                user.getPassword(), new ArrayList<>());
    }


    public User getUserByName(String username) {
        return userRepository.findByName(username);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDto(user);
    }


    public void registerUser(UserDto userDto) throws UserAlreadyExistsException {
        if (userRepository.existsByName(userDto.getName())) {
            log.warn("Registration attempt with existing email: {}", userDto.getEmail());
            throw new UserAlreadyExistsException("User with such name already exists");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.warn("Registration attempt with existing username: {}", userDto.getName());
            throw new UserAlreadyExistsException("User with such mail already exists");
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setCity(userDto.getCity());
        user.setAddress(userDto.getAddress());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        log.info("User registered successfully with email: {}", userDto.getEmail());

    }

    public Page<User> getUsersPage(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1 , pageSize, Sort.by("name").ascending());
        return userRepository.findAll(pageable);
    }

}
