package com.yaskal.library.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private String publisher;

    private String isbn;

    private LocalDate publicationDate;

    private String genres;

    private int numberOfPages;

    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contributor_id")
    private User contributor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_keeper_id")
    private User currentKeeper;

    @OneToMany(mappedBy = "book")
    private List<LoanHistory> loanHistories = new ArrayList<>();

    public boolean isAvailable() {
        return currentKeeper == null;
    }

}