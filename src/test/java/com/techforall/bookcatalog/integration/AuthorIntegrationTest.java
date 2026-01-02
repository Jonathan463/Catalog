package com.techforall.bookcatalog.integration;

import com.techforall.bookcatalog.model.dto.request.AuthorRequest;
import com.techforall.bookcatalog.model.entity.Author;
import com.techforall.bookcatalog.repository.AuthorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Author API Integration Tests")
class AuthorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorRepository authorRepository;

    private Author savedAuthor;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();

        Author author = new Author();
        author.setName("George");
        author.setSurname("Orwell");
        author.setBirthYear(1903);
        savedAuthor = authorRepository.save(author);
    }

    @Nested
    @DisplayName("GET /authors Tests")
    class GetAllAuthorsTests {

        @Test
        @DisplayName("Should return paginated list of authors")
        void getAllAuthors_ShouldReturnAllAuthors() throws Exception {
            mockMvc.perform(get("/authors"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].fullName", is("George Orwell")))
                    .andExpect(jsonPath("$.totalElements", is(1)));
        }
    }

    @Nested
    @DisplayName("GET /authors/{id} Tests")
    class GetAuthorByIdTests {

        @Test
        @DisplayName("Should return author by ID")
        void getAuthorById_ShouldReturnAuthor() throws Exception {
            mockMvc.perform(get("/authors/" + savedAuthor.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("George")))
                    .andExpect(jsonPath("$.surname", is("Orwell")))
                    .andExpect(jsonPath("$.birthYear", is(1903)));
        }

        @Test
        @DisplayName("Should return 404 for non-existent author")
        void getAuthorById_ShouldReturn404() throws Exception {
            mockMvc.perform(get("/authors/99999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /authors Tests")
    class CreateAuthorTests {

        @Test
        @DisplayName("Should create new author")
        void createAuthor_ShouldCreateAndReturnAuthor() throws Exception {
            AuthorRequest newAuthor = AuthorRequest.builder()
                    .name("J.R.R.")
                    .surname("Tolkien")
                    .birthYear(1892)
                    .build();

            mockMvc.perform(post("/authors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newAuthor)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.name", is("J.R.R.")))
                    .andExpect(jsonPath("$.surname", is("Tolkien")));
        }

        @Test
        @DisplayName("Should return 400 for invalid author")
        void createAuthor_ShouldReturn400ForInvalidInput() throws Exception {
            AuthorRequest invalidAuthor = AuthorRequest.builder()
                    .name("")
                    .surname("")
                    .birthYear(null)
                    .build();

            mockMvc.perform(post("/authors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidAuthor)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /authors/{id} Tests")
    class UpdateAuthorTests {

        @Test
        @DisplayName("Should update existing author")
        void updateAuthor_ShouldUpdateAndReturnAuthor() throws Exception {
            AuthorRequest updateRequest = AuthorRequest.builder()
                    .name("Eric")
                    .surname("Blair")
                    .birthYear(1903)
                    .build();

            mockMvc.perform(put("/authors/" + savedAuthor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Eric")))
                    .andExpect(jsonPath("$.surname", is("Blair")));
        }
    }

    @Nested
    @DisplayName("DELETE /authors/{id} Tests")
    class DeleteAuthorTests {

        @Test
        @DisplayName("Should delete author without books")
        void deleteAuthor_ShouldDeleteAuthor() throws Exception {
            mockMvc.perform(delete("/authors/" + savedAuthor.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/authors/" + savedAuthor.getId()))
                    .andExpect(status().isNotFound());
        }
    }
}

