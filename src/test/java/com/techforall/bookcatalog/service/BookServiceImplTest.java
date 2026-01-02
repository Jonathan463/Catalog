package com.techforall.bookcatalog.service;

import com.techforall.bookcatalog.exception.ResourceNotFoundException;
import com.techforall.bookcatalog.model.dto.request.BookRequest;
import com.techforall.bookcatalog.model.dto.response.AuthorSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.BookResponse;
import com.techforall.bookcatalog.model.dto.response.BookSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.PageResponse;
import com.techforall.bookcatalog.model.entity.Author;
import com.techforall.bookcatalog.model.entity.Book;
import com.techforall.bookcatalog.repository.AuthorRepository;
import com.techforall.bookcatalog.repository.BookRepository;
import com.techforall.bookcatalog.service.impl.BookServiceImpl;
import com.techforall.bookcatalog.utility.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Mockito Tests")
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookResponse bookResponse;
    private BookSummaryResponse bookSummaryResponse;
    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("George");
        author.setSurname("Orwell");
        author.setBirthYear(1903);
        author.setBooks(new HashSet<>());

        book = new Book();
        book.setId(1L);
        book.setTitle("1984");
        book.setPublisher("Secker & Warburg");
        book.setEdition("First Edition");
        book.setPublishedDate(LocalDate.of(1949, 6, 8));
        book.setAuthors(new HashSet<>(Set.of(author)));

        Set<AuthorSummaryResponse> authorSummaries = Set.of(
                AuthorSummaryResponse.builder().id(1L).fullName("George Orwell").build());

        bookResponse = BookResponse.builder()
                .id(1L)
                .title("1984")
                .authors(authorSummaries)
                .publisher("Secker & Warburg")
                .edition("First Edition")
                .publishedDate(LocalDate.of(1949, 6, 8))
                .build();

        bookSummaryResponse = BookSummaryResponse.builder()
                .id(1L)
                .title("1984")
                .publisher("Secker & Warburg")
                .build();
    }

    @Nested
    @DisplayName("getAllBooks Tests")
    class GetAllBooksTests {

        @Test
        @DisplayName("Should return paginated list of book summaries")
        void getAllBooks_ShouldReturnBookSummaries() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

            when(bookRepository.findAll(pageable)).thenReturn(bookPage);
            when(entityMapper.toBookSummaryResponse(book)).thenReturn(bookSummaryResponse);

            PageResponse<BookSummaryResponse> result = bookService.getAllBooks(pageable);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals("1984", result.getContent().get(0).getTitle());
            assertEquals(0, result.getPageNumber());
            assertEquals(1, result.getTotalElements());
            verify(bookRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Should return empty page when no books exist")
        void getAllBooks_ShouldReturnEmptyPageWhenNoBooks() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Book> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(bookRepository.findAll(pageable)).thenReturn(emptyPage);

            PageResponse<BookSummaryResponse> result = bookService.getAllBooks(pageable);

            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("getBookById Tests")
    class GetBookByIdTests {

        @Test
        @DisplayName("Should return book when found")
        void getBookById_ShouldReturnBook() {
            when(bookRepository.findByIdWithAuthors(1L)).thenReturn(Optional.of(book));
            when(entityMapper.toBookResponse(book)).thenReturn(bookResponse);

            BookResponse result = bookService.getBookById(1L);

            assertNotNull(result);
            assertEquals("1984", result.getTitle());
            verify(bookRepository).findByIdWithAuthors(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book not found")
        void getBookById_ShouldThrowExceptionWhenNotFound() {
            when(bookRepository.findByIdWithAuthors(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> bookService.getBookById(999L));
        }
    }

    @Nested
    @DisplayName("createBook Tests")
    class CreateBookTests {

        @Test
        @DisplayName("Should create and return new book with authors")
        void createBook_ShouldCreateBookWithAuthors() {
            BookRequest request = BookRequest.builder()
                    .title("1984")
                    .authorIds(Set.of(1L))
                    .publisher("Secker & Warburg")
                    .edition("First Edition")
                    .publishedDate(LocalDate.of(1949, 6, 8))
                    .build();

            Book newBook = new Book();
            newBook.setTitle("1984");
            newBook.setAuthors(new HashSet<>());

            when(entityMapper.toBookEntity(request)).thenReturn(newBook);
            when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
            when(bookRepository.save(any(Book.class))).thenReturn(book);
            when(entityMapper.toBookResponse(book)).thenReturn(bookResponse);

            BookResponse result = bookService.createBook(request);

            assertNotNull(result);
            assertEquals("1984", result.getTitle());
            verify(bookRepository).save(any(Book.class));
        }

        @Test
        @DisplayName("Should create book without authors when authorIds is empty")
        void createBook_ShouldCreateBookWithoutAuthors() {
            BookRequest request = BookRequest.builder()
                    .title("1984")
                    .authorIds(Set.of())
                    .publisher("Secker & Warburg")
                    .edition("First Edition")
                    .publishedDate(LocalDate.of(1949, 6, 8))
                    .build();

            Book newBook = new Book();
            newBook.setTitle("1984");
            newBook.setAuthors(new HashSet<>());

            when(entityMapper.toBookEntity(request)).thenReturn(newBook);
            when(bookRepository.save(any(Book.class))).thenReturn(book);
            when(entityMapper.toBookResponse(book)).thenReturn(bookResponse);

            BookResponse result = bookService.createBook(request);

            assertNotNull(result);
            verify(authorRepository, never()).findById(anyLong());
        }
    }

    @Nested
    @DisplayName("updateBook Tests")
    class UpdateBookTests {

        @Test
        @DisplayName("Should update existing book")
        void updateBook_ShouldUpdateBook() {
            BookRequest updateRequest = BookRequest.builder()
                    .title("Nineteen Eighty-Four")
                    .authorIds(Set.of(1L))
                    .publisher("Penguin")
                    .edition("New Edition")
                    .publishedDate(LocalDate.of(1950, 1, 1))
                    .build();

            when(bookRepository.findByIdWithAuthors(1L)).thenReturn(Optional.of(book));
            when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
            when(bookRepository.save(book)).thenReturn(book);
            when(entityMapper.toBookResponse(book)).thenReturn(bookResponse);

            BookResponse result = bookService.updateBook(1L, updateRequest);

            assertNotNull(result);
            verify(entityMapper).updateBookFromRequest(book, updateRequest);
            verify(bookRepository).save(book);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existent book")
        void updateBook_ShouldThrowExceptionWhenNotFound() {
            BookRequest updateRequest = BookRequest.builder()
                    .title("Updated Title")
                    .authorIds(Set.of())
                    .publisher("Publisher")
                    .edition("Edition")
                    .publishedDate(LocalDate.now())
                    .build();

            when(bookRepository.findByIdWithAuthors(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> bookService.updateBook(999L, updateRequest));
        }
    }

    @Nested
    @DisplayName("deleteBook Tests")
    class DeleteBookTests {

        @Test
        @DisplayName("Should delete existing book")
        void deleteBook_ShouldDeleteBook() {
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

            assertDoesNotThrow(() -> bookService.deleteBook(1L));
            verify(bookRepository).delete(book);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent book")
        void deleteBook_ShouldThrowExceptionWhenNotFound() {
            when(bookRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> bookService.deleteBook(999L));
        }
    }
}

