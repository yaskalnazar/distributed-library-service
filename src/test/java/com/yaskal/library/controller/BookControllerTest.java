package com.yaskal.library.controller;

import com.yaskal.library.model.BookDto;
import com.yaskal.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    public void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void testGetBooks() throws Exception {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("john");
        mockMvc.perform(get("/api/v1/books/main").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attribute("username", "john"));
    }

    @Test
    public void testGetAddBookForm() throws Exception {
        mockMvc.perform(get("/api/v1/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("addBook"))
                .andExpect(model().attributeExists("bookDto"))
                .andExpect(model().attribute("bookDto", new BookDto()));
    }

    @Test
    public void testCreateBook() throws Exception {
        BookDto bookDto = new BookDto();
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("john");
        mockMvc.perform(post("/api/v1/books").flashAttr("bookDto", bookDto).principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("addBook"))
                .andExpect(model().attribute("message", "Book added successfully."));
        verify(bookService, times(1)).createBook(eq(bookDto), eq(mockPrincipal));
    }

    @Test
    public void testShowEditBookForm() throws Exception {
        BookDto bookDto = new BookDto();
        when(bookService.getBookById(1L)).thenReturn(bookDto);
        mockMvc.perform(get("/api/v1/books/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("editBook"))
                .andExpect(model().attribute("book", bookDto));
    }

    @Test
    public void testEditBook() throws Exception {
        BookDto bookDto = new BookDto();
        when(bookService.updateBook(1L, bookDto)).thenReturn(bookDto);
        mockMvc.perform(post("/api/v1/books/1/edit").flashAttr("book", bookDto))
                .andExpect(status().isOk())
                .andExpect(view().name("editBook"))
                .andExpect(model().attribute("message", "Book edited successfully."))
                .andExpect(model().attribute("book", bookDto));
    }

    @Test
    @DisplayName("Test showDashboard method")
    void testShowDashboard() {
        List<BookDto> allBooks = new ArrayList<>();
        allBooks.add(new BookDto());
        allBooks.add(new BookDto());
        when(bookService.getAllBooks()).thenReturn(allBooks);

        ModelAndView result = bookController.showDashboard();

        verify(bookService, times(1)).getAllBooks();
        assertEquals("dashboard", result.getViewName());
        assertEquals(allBooks, result.getModel().get("allBooks"));
    }

    @Test
    @DisplayName("Test filterBooks method with all parameters")
    void testFilterBooksWithAllParams() {
        List<BookDto> filteredBooks = new ArrayList<>();
        filteredBooks.add(new BookDto());
        filteredBooks.add(new BookDto());
        String title = "Book Title";
        String author = "Author Name";
        String genres = "Fiction, Drama";
        LocalDate startDate = LocalDate.of(2021, 1, 1);
        LocalDate endDate = LocalDate.of(2021, 12, 31);
        when(bookService.findByFilters(title, author, genres, startDate, endDate)).thenReturn(filteredBooks);

        ModelAndView result = bookController.filterBooks(title, author, genres, startDate, endDate);

        verify(bookService, times(1)).findByFilters(title, author, genres, startDate, endDate);
        assertEquals("dashboard", result.getViewName());
        assertEquals(filteredBooks, result.getModel().get("allBooks"));
        assertEquals(title, result.getModel().get("title"));
        assertEquals(author, result.getModel().get("author"));
        assertEquals(genres, result.getModel().get("genres"));
        assertEquals(startDate, result.getModel().get("startDate"));
        assertEquals(endDate, result.getModel().get("endDate"));
    }

    @Test
    @DisplayName("Test filterBooks method with no parameters")
    void testFilterBooksWithNoParams() {
        List<BookDto> allBooks = new ArrayList<>();
        allBooks.add(new BookDto());
        allBooks.add(new BookDto());
        when(bookService.findByFilters(null, null, null, null, null)).thenReturn(allBooks);

        ModelAndView result = bookController.filterBooks(null, null, null, null, null);

        verify(bookService, times(1)).findByFilters(null, null, null, null, null);
        assertEquals("dashboard", result.getViewName());
        assertEquals(allBooks, result.getModel().get("allBooks"));
    }

    @Test
    @DisplayName("Test getAllBooks method")
    void testGetAllBooks() {
        List<BookDto> allBooks = new ArrayList<>();
        allBooks.add(new BookDto());
        allBooks.add(new BookDto());
        when(bookService.getAllBooks()).thenReturn(allBooks);

        ModelAndView result = bookController.getAllBooks();

        verify(bookService, times(1)).getAllBooks();
        assertEquals("books", result.getViewName());
        assertEquals(allBooks, result.getModel().get("books"));
    }

}