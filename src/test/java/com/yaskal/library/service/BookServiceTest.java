package com.yaskal.library.service;

import com.yaskal.library.exception.ResourceNotFoundException;
import com.yaskal.library.mapping.BookMapper;
import com.yaskal.library.model.Book;
import com.yaskal.library.model.BookDto;
import com.yaskal.library.model.User;
import com.yaskal.library.model.UserDto;
import com.yaskal.library.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private Principal principal;
    @Mock
    private EntityManager entityManager;
    @Mock
    private CriteriaQuery<Book> criteriaQuery;
    @Mock
    private Root<Book> root;
    @Mock
    private TypedQuery<Book> typedQuery;
    @Mock
    private CriteriaBuilder criteriaBuilder;
    @InjectMocks
    private BookService bookService;



    private Book book1, book2;
    private BookDto bookDto1, bookDto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("The Adventures of Tom Sawyer");
        book1.setAuthor("Mark Twain");
        book1.setPublicationDate(LocalDate.of(1876, 1, 1));
        User user1 = new User();
        user1.setId(1L);
        book1.setContributor(user1);

        bookDto1 = new BookDto();
        bookDto1.setId(1L);
        bookDto1.setTitle("The Adventures of Tom Sawyer");
        bookDto1.setAuthor("Mark Twain");
        bookDto1.setPublicationDate(LocalDate.of(1876, 1, 1));
        bookDto1.setContributorId(1L);

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Harry Potter and the Philosopher's Stone");
        book2.setAuthor("J.K. Rowling");
        book2.setPublicationDate(LocalDate.of(1997, 6, 26));
        User user2 = new User();
        user2.setId(2L);
        book2.setContributor(user2);

        bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setTitle("Harry Potter and the Philosopher's Stone");
        bookDto2.setAuthor("J.K. Rowling");
        bookDto2.setPublicationDate(LocalDate.of(1997, 6, 26));
        bookDto2.setContributorId(2L);
    }

    @Test
    void testGetAllBooks() {
        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);

        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(bookDto1);
        bookDtos.add(bookDto2);

        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        assertEquals(bookDtos, bookService.getAllBooks());
    }

    @Test
    void testGetBookById() {
        Long id = 1L;

        when(bookRepository.findById(id)).thenReturn(Optional.of(book1));
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);

        assertEquals(bookDto1, bookService.getBookById(id));
    }

    @Test
    void testGetBookById_ResourceNotFoundException() {
        Long id = 1L;

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(id));
    }

    @Test
    void testCreateBook() {
        Book book = new Book();
        book.setTitle("The Great Gatsby");
        book.setAuthor("F. Scott Fitzgerald");
        book.setPublicationDate(LocalDate.of(1925, 4, 10));
        User user = new User();
        user.setId(3L);

        BookDto bookDto = new BookDto();
        bookDto.setTitle("The Great Gatsby");
        bookDto.setAuthor("F. Scott Fitzgerald");
        bookDto.setPublicationDate(LocalDate.of(1925, 4, 10));
        bookDto.setContributorId(3L);

        when(userService.getUserByName(user.getName())).thenReturn(user);
        when(bookMapper.toEntity(bookDto)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookRepository.save(book)).thenReturn(book);

        assertEquals(bookDto, bookService.createBook(bookDto, principal));
    }

    @Test
    void testUpdateBook() {
        Long id = 1L;
        Book book = new Book();
        book.setId(id);
        book.setTitle("The Adventures of Tom Sawyer");
        book.setAuthor("Mark Twain");
        book.setPublicationDate(LocalDate.of(1876, 1, 1));
        User user = new User();
        user.setId(1L);
        book.setContributor(user);

        BookDto bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setTitle("The Adventures of Huckleberry Finn");
        bookDto.setAuthor("Mark Twain");
        bookDto.setPublicationDate(LocalDate.of(1884, 12, 10));
        bookDto.setContributorId(1L);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toEntity(bookDto)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookRepository.save(book)).thenReturn(book);

        assertEquals(bookDto, bookService.updateBook(id, bookDto));
    }

    @Test
    void testFindByContributor() {
        Long contributorId = 1L;

        List<Book> books = new ArrayList<>();
        books.add(book1);

        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(bookDto1);

        when(bookRepository.findByContributor_Id(contributorId)).thenReturn(books);
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);

        assertEquals(bookDtos, bookService.findByContributor(contributorId));
    }

    @Test
    void testFindByCurrentKeeper() {
        Long keeperId = 2L;

        List<Book> books = new ArrayList<>();
        books.add(book2);

        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(bookDto2);

        when(userService.getUserById(keeperId)).thenReturn(new UserDto());
        when(bookRepository.findByCurrentKeeper_Id(keeperId)).thenReturn(books);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        assertEquals(bookDtos, bookService.findByCurrentKeeper(keeperId));
    }
    @Test
    void testFindByFilters_WithTitle() {
        String title = "Harry Potter";
        String author = null;
        String genres = null;
        LocalDate startDate = null;
        LocalDate endDate = null;

        List<Book> books = new ArrayList<>();
        books.add(book2);

        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(bookDto2);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Book.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Book.class)).thenReturn(root);

        Predicate expectedPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");

        when(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%")).thenReturn(expectedPredicate);

        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(books);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        List<BookDto> actualResult = bookService.findByFilters(title, author, genres, startDate, endDate);
        assertEquals(bookDtos, actualResult);
    }

    @Test
    void testFindByFilters_WithAuthor() {
        String title = null;
        String author = "J.K. Rowling";
        String genres = null;
        LocalDate startDate = null;
        LocalDate endDate = null;

        List<Book> books = new ArrayList<>();
        books.add(book2);

        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(bookDto2);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Book.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Book.class)).thenReturn(root);

        Predicate expectedPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + author.toLowerCase() + "%");

        when(criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + author.toLowerCase() + "%")).thenReturn(expectedPredicate);

        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(books);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        List<BookDto> actualResult = bookService.findByFilters(title, author, genres, startDate, endDate);
        assertEquals(bookDtos, actualResult);
    }

    @Test
    void testFindByFilters_WithGenres() {
        String title = null;
        String author = null;
        String genres = "fiction";
        LocalDate startDate = null;
        LocalDate endDate = null;

        List<Book> books = new ArrayList<>();
        books.add(book2);

        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(bookDto2);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Book.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Book.class)).thenReturn(root);

        Predicate expectedPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("genres")), "%" + genres.toLowerCase() + "%");

        when(criteriaBuilder.like(criteriaBuilder.lower(root.get("genres")), "%" + genres.toLowerCase() + "%")).thenReturn(expectedPredicate);

        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(books);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        List<BookDto> actualResult = bookService.findByFilters(title, author, genres, startDate, endDate);
        assertEquals(bookDtos, actualResult);
    }

    @Test
    void testFindByFilters_WithAllParameters() {
        // Arrange
        String title = "the";
        String author = "stephen";
        String genres = "fiction";
        LocalDate startDate = LocalDate.of(2010, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 12, 31);

        List<Book> books = new ArrayList<>();
        books.add(book2);

        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(bookDto2);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Book.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Book.class)).thenReturn(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + author.toLowerCase() + "%"));
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("genres")), "%" + genres.toLowerCase() + "%"));
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("publicationDate"), startDate));
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("publicationDate"), endDate));

        Predicate expectedPredicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        when(criteriaBuilder.and(predicates.toArray(new Predicate[0]))).thenReturn(expectedPredicate);

        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(books);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        // Act
        List<BookDto> actualResult = bookService.findByFilters(title, author, genres, startDate, endDate);

        // Assert
        assertEquals(bookDtos, actualResult);
    }


}