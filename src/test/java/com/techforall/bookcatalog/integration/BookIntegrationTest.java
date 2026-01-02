package com.techforall.bookcatalog.integration;

import com.techforall.bookcatalog.model.dto.request.BookRequest;
import com.techforall.bookcatalog.model.entity.Author;
import com.techforall.bookcatalog.model.entity.Book;
import com.techforall.bookcatalog.repository.AuthorRepository;
import com.techforall.bookcatalog.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Book API Integration Tests")
class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Author savedAuthor;
    private Book savedBook;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();

        Author author = new Author();
        author.setName("George");
        author.setSurname("Orwell");
        author.setBirthYear(1903);
        savedAuthor = authorRepository.save(author);

        Book book = new Book();
        book.setTitle("1984");
        book.setPublisher("Secker & Warburg");
        book.setEdition("First Edition");
        book.setPublishedDate(LocalDate.of(1949, 6, 8));
        book.setAuthors(new HashSet<>(Set.of(savedAuthor)));
        savedBook = bookRepository.save(book);
    }

    @Nested
    @DisplayName("GET /books Tests")
    class GetAllBooksTests {

        @Test
        @DisplayName("Should return paginated list of books")
        void getAllBooks_ShouldReturnAllBooks() throws Exception {
            mockMvc.perform(get("/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title", is("1984")))
                    .andExpect(jsonPath("$.totalElements", is(1)));
        }
    }

    @Nested
    @DisplayName("GET /books/{id} Tests")
    class GetBookByIdTests {

        @Test
        @DisplayName("Should return book by ID with authors")
        void getBookById_ShouldReturnBookWithAuthors() throws Exception {
            mockMvc.perform(get("/books/" + savedBook.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("1984")))
                    .andExpect(jsonPath("$.publisher", is("Secker & Warburg")))
                    .andExpect(jsonPath("$.authors", hasSize(1)))
                    .andExpect(jsonPath("$.authors[0].fullName", is("George Orwell")));
        }

        @Test
        @DisplayName("Should return 404 for non-existent book")
        void getBookById_ShouldReturn404() throws Exception {
            mockMvc.perform(get("/books/99999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /books Tests")
    class CreateBookTests {

        @Test
        @DisplayName("Should create new book with authors")
        void createBook_ShouldCreateBookWithAuthors() throws Exception {
            BookRequest newBook = BookRequest.builder()
                    .title("Animal Farm")
                    .authorIds(Set.of(savedAuthor.getId()))
                    .publisher("Secker & Warburg")
                    .edition("First Edition")
                    .publishedDate(LocalDate.of(1945, 8, 17))
                    .build();

            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newBook)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.title", is("Animal Farm")))
                    .andExpect(jsonPath("$.authors", hasSize(1)));
        }

        @Test
        @DisplayName("Should return 201 when creating book without authors")
        void createBook_ShouldReturn400WhenNoAuthors() throws Exception {
            BookRequest newBook = BookRequest.builder()
                    .title("Unknown Book")
                    .authorIds(Set.of())
                    .publisher("Publisher")
                    .edition("Edition")
                    .publishedDate(LocalDate.now())
                    .build();

            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newBook)))
                    .andExpect(status().isCreated());

        }
    }

    @Nested
    @DisplayName("PUT /books/{id} Tests")
    class UpdateBookTests {

        @Test
        @DisplayName("Should update existing book")
        void updateBook_ShouldUpdateBook() throws Exception {
            BookRequest updateRequest = BookRequest.builder()
                    .title("Nineteen Eighty-Four")
                    .authorIds(Set.of(savedAuthor.getId()))
                    .publisher("Penguin")
                    .edition("New Edition")
                    .publishedDate(LocalDate.now())
                    .build();

            mockMvc.perform(put("/books/" + savedBook.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title", is("Nineteen Eighty-Four")))
                    .andExpect(jsonPath("$.publisher", is("Penguin")));
        }
    }

    @Nested
    @DisplayName("DELETE /books/{id} Tests")
    class DeleteBookTests {

        @Test
        @DisplayName("Should delete book")
        void deleteBook_ShouldDeleteBook() throws Exception {
            mockMvc.perform(delete("/books/" + savedBook.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/books/" + savedBook.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent book")
        void deleteBook_ShouldReturn404() throws Exception {
            mockMvc.perform(delete("/books/99999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Author-Book Relationship Tests")
    class AuthorBookRelationshipTests {

        @Test
        @DisplayName("Should not allow deleting author with books")
        void deleteAuthorWithBooks_ShouldReturn409() throws Exception {
            mockMvc.perform(delete("/authors/" + savedAuthor.getId()))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should allow deleting author after deleting the book")
        void deleteAuthorAfterDeletingBook_ShouldSucceed() throws Exception {

            mockMvc.perform(delete("/books/" + savedBook.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(delete("/authors/" + savedAuthor.getId()))
                    .andExpect(status().isNoContent());
        }
    }
}

