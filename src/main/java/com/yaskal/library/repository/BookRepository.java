package com.yaskal.library.repository;

import com.yaskal.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByContributor_Id(Long contributorId);
    List<Book> findByCurrentKeeper_Id(Long currentKeeperId);

}
