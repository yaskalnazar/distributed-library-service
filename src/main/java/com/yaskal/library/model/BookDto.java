package com.yaskal.library.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private LocalDate publicationDate;
    private String genres;
    private int numberOfPages;
    private String tags;
    private Long contributorId;
    private String currentKeeperId;

}
