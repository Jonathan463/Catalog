package com.techforall.bookcatalog.unit;

import com.techforall.bookcatalog.model.dto.request.AuthorRequest;
import com.techforall.bookcatalog.model.dto.request.BookRequest;
import com.techforall.bookcatalog.model.dto.response.*;
import com.techforall.bookcatalog.model.dto.response.AuthorResponse;
import com.techforall.bookcatalog.model.dto.response.AuthorSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.BookResponse;
import com.techforall.bookcatalog.model.dto.response.BookSummaryResponse;
import com.techforall.bookcatalog.model.entity.Author;
import com.techforall.bookcatalog.model.entity.Book;
import com.techforall.bookcatalog.utility.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("EntityMapper Unit Tests")
class EntityMapperTest {

    private EntityMapper entityMapper;

    @BeforeEach
    void setUp() {
        entityMapper = new EntityMapper();
    }

    @Nested
    @DisplayName("Author Mapping Tests")
    class AuthorMappingTests {

        @Test
        @DisplayName("Should map Author entity to AuthorResponse")
        void toAuthorResponse_ShouldMapCorrectly() {
            Author author = createAuthor(1L, "George", "Orwell", 1903);

            AuthorResponse response = entityMapper.toAuthorResponse(author);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("George", response.getName());
            assertEquals("Orwell", response.getSurname());
            assertEquals("George Orwell", response.getFullName());
            assertEquals(1903, response.getBirthYear());
        }


        @Test
        @DisplayName("Should map Author entity to AuthorSummaryResponse")
        void toAuthorSummaryResponse_ShouldMapCorrectly() {
            Author author = createAuthor(1L, "George", "Orwell", 1903);

            AuthorSummaryResponse response = entityMapper.toAuthorSummaryResponse(author);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("George Orwell", response.getFullName());
        }

        @Test
        @DisplayName("Should return null when mapping null Author to AuthorSummaryResponse")
        void toAuthorSummaryResponse_ShouldReturnNullForNullInput() {
            assertNull(entityMapper.toAuthorSummaryResponse(null));
        }

        @Test
        @DisplayName("Should map AuthorRequest to Author entity")
        void toAuthorEntity_ShouldMapCorrectly() {
            AuthorRequest request = AuthorRequest.builder()
                    .name("George")
                    .surname("Orwell")
                    .birthYear(1903)
                    .build();

            Author author = entityMapper.toAuthorEntity(request);

            assertNotNull(author);
            assertNull(author.getId());
            assertEquals("George", author.getName());
            assertEquals("Orwell", author.getSurname());
            assertEquals(1903, author.getBirthYear());
        }

        @Test
        @DisplayName("Should return null when mapping null AuthorRequest to Author entity")
        void toAuthorEntity_ShouldReturnNullForNullInput() {
            assertNull(entityMapper.toAuthorEntity(null));
        }

        @Test
        @DisplayName("Should update Author entity from AuthorRequest")
        void updateAuthorFromRequest_ShouldUpdateAllFields() {
            Author author = createAuthor(1L, "George", "Orwell", 1903);
            AuthorRequest request = AuthorRequest.builder()
                    .name("Eric")
                    .surname("Blair")
                    .birthYear(1903)
                    .build();

            entityMapper.updateAuthorFromRequest(author, request);

            assertEquals("Eric", author.getName());
            assertEquals("Blair", author.getSurname());
            assertEquals(1903, author.getBirthYear());
        }

        @Test
        @DisplayName("Should only update non-null fields from AuthorRequest")
        void updateAuthorFromRequest_ShouldOnlyUpdateNonNullFields() {
            Author author = createAuthor(1L, "George", "Orwell", 1903);
            AuthorRequest request = AuthorRequest.builder()
                    .name("Eric")
                    .build();

            entityMapper.updateAuthorFromRequest(author, request);

            assertEquals("Eric", author.getName());
            assertEquals("Orwell", author.getSurname());
            assertEquals(1903, author.getBirthYear());
        }
    }

    @Nested
    @DisplayName("Book Mapping Tests")
    class BookMappingTests {

        @Test
        @DisplayName("Should map Book entity to BookResponse")
        void toBookResponse_ShouldMapCorrectly() {
            Author author = createAuthor(1L, "George", "Orwell", 1903);
            Book book = createBook(1L, "1984", "Secker & Warburg", "First Edition",
                    LocalDate.of(1949, 6, 8), Set.of(author));

            BookResponse response = entityMapper.toBookResponse(book);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("1984", response.getTitle());
            assertEquals("Secker & Warburg", response.getPublisher());
            assertEquals("First Edition", response.getEdition());
            assertEquals(LocalDate.of(1949, 6, 8), response.getPublishedDate());
            assertNotNull(response.getAuthors());
            assertEquals(1, response.getAuthors().size());
        }

        @Test
        @DisplayName("Should return null when mapping null Book to BookResponse")
        void toBookResponse_ShouldReturnNullForNullInput() {
            assertNull(entityMapper.toBookResponse(null));
        }

        @Test
        @DisplayName("Should map Book entity to BookSummaryResponse")
        void toBookSummaryResponse_ShouldMapCorrectly() {
            Book book = createBook(1L, "1984", "Secker & Warburg", "First Edition",
                    LocalDate.of(1949, 6, 8), new HashSet<>());

            BookSummaryResponse response = entityMapper.toBookSummaryResponse(book);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("1984", response.getTitle());
            assertEquals("Secker & Warburg", response.getPublisher());
        }

        @Test
        @DisplayName("Should map BookRequest to Book entity")
        void toBookEntity_ShouldMapCorrectly() {
            BookRequest request = BookRequest.builder()
                    .title("1984")
                    .authorIds(Set.of(1L))
                    .publisher("Secker & Warburg")
                    .edition("First Edition")
                    .publishedDate(LocalDate.of(1949, 6, 8))
                    .build();

            Book book = entityMapper.toBookEntity(request);

            assertNotNull(book);
            assertNull(book.getId());
            assertEquals("1984", book.getTitle());
            assertEquals("Secker & Warburg", book.getPublisher());
            assertEquals("First Edition", book.getEdition());
            assertEquals(LocalDate.of(1949, 6, 8), book.getPublishedDate());
        }
    }

    // Helper methods
    private Author createAuthor(Long id, String name, String surname, Integer birthYear) {
        Author author = new Author();
        author.setId(id);
        author.setName(name);
        author.setSurname(surname);
        author.setBirthYear(birthYear);
        return author;
    }

    private Book createBook(Long id, String title, String publisher, String edition,
                            LocalDate publishedDate, Set<Author> authors) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setPublisher(publisher);
        book.setEdition(edition);
        book.setPublishedDate(publishedDate);
        book.setAuthors(authors);
        return book;
    }
}

