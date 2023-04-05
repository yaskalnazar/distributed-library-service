package com.yaskal.library.repository;

import com.yaskal.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);
    boolean existsByName(String name);
    boolean existsByEmail(String email);

}