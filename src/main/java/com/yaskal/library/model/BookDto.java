package com.yaskal.library.model;

import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private Integer yearPublished;
    private String imageUrl;
    private String description;
    private Boolean available;
    private String contributorId;
}
