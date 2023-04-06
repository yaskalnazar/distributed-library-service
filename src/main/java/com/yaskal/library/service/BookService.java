package com.yaskal.library.service;

import com.yaskal.library.exception.ResourceNotFoundException;
import com.yaskal.library.model.Book;
import com.yaskal.library.model.BookDto;
import com.yaskal.library.repository.BookRepository;
import com.yaskal.library.mapping.BookMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BookMapper bookMapper;
    @PersistenceContext
    private EntityManager entityManager;

    public List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::toDto).collect(Collectors.toList());
    }


    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        return bookMapper.toDto(book);
    }


    public BookDto createBook(BookDto bookDto, Principal principal) {
        Long userId = userService.getUserByName(principal.getName()).getId();
        bookDto.setContributorId(userId);
        Book book = bookMapper.toEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }


    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        Book updatedBook = bookMapper.toEntity(bookDto);
        updatedBook.setId(book.getId());
        Book savedBook = bookRepository.save(updatedBook);
        return bookMapper.toDto(savedBook);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
        bookRepository.delete(book);
    }

    public List<BookDto> findByFilters(String title, String author, String genres, LocalDate startDate, LocalDate endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        List<Predicate> predicates = new ArrayList<>();
        if (title != null && !title.isEmpty()) {
            predicates.add(cb.like(cb.lower(book.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (author != null && !author.isEmpty()) {
            predicates.add(cb.like(cb.lower(book.get("author")), "%" + author.toLowerCase() + "%"));
        }
        if (genres != null && !genres.isEmpty()) {
            predicates.add(cb.like(cb.lower(book.get("genres")), "%" + genres.toLowerCase() + "%"));
        }
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(book.get("publicationDate"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(book.get("publicationDate"), endDate));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Book> query = entityManager.createQuery(cq);

        List<Book> resultList = query.getResultList();
        return resultList.stream().map(bookMapper::toDto).collect(Collectors.toList());
    }
}
