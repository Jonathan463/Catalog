package com.techforall.bookcatalog.controller;

import com.techforall.bookcatalog.exception.AuthorHasBooksException;
import com.techforall.bookcatalog.exception.ResourceNotFoundException;
import com.techforall.bookcatalog.model.dto.request.AuthorRequest;
import com.techforall.bookcatalog.model.dto.response.AuthorResponse;
import com.techforall.bookcatalog.model.dto.response.AuthorSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.PageResponse;
import com.techforall.bookcatalog.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthorController.class)
@DisplayName("AuthorController MockMvc Tests")
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    private AuthorResponse authorResponse;
    private AuthorSummaryResponse authorSummaryResponse;

    @BeforeEach
    void setUp() {
        authorResponse = AuthorResponse.builder()
                .id(1L)
                .name("George")
                .surname("Orwell")
                .fullName("George Orwell")
                .birthYear(1903)
                .build();

        authorSummaryResponse = AuthorSummaryResponse.builder()
                .id(1L)
                .fullName("George Orwell")
                .build();
    }

    @Nested
    @DisplayName("GET /authors Tests")
    class GetAllAuthorsTests {

        @Test
        @DisplayName("Should return paginated list of authors")
        void getAllAuthors_ShouldReturnAuthorList() throws Exception {
            PageResponse<AuthorSummaryResponse> pageResponse = PageResponse.<AuthorSummaryResponse>builder()
                    .content(List.of(authorSummaryResponse))
                    .pageNumber(0)
                    .pageSize(20)
                    .totalElements(1)
                    .totalPages(1)
                    .first(true)
                    .last(true)
                    .build();

            when(authorService.getAllAuthors(any(Pageable.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/authors"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].fullName", is("George Orwell")))
                    .andExpect(jsonPath("$.totalElements", is(1)));
        }

        @Test
        @DisplayName("Should return empty page when no authors")
        void getAllAuthors_ShouldReturnEmptyPage() throws Exception {
            PageResponse<AuthorSummaryResponse> emptyPage = PageResponse.<AuthorSummaryResponse>builder()
                    .content(List.of())
                    .pageNumber(0)
                    .pageSize(20)
                    .totalElements(0)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .build();

            when(authorService.getAllAuthors(any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/authors"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)));
        }
    }

    @Nested
    @DisplayName("GET /authors/{id} Tests")
    class GetAuthorByIdTests {

        @Test
        @DisplayName("Should return author when found")
        void getAuthorById_ShouldReturnAuthor() throws Exception {
            when(authorService.getAuthorById(1L)).thenReturn(authorResponse);

            mockMvc.perform(get("/authors/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("George")))
                    .andExpect(jsonPath("$.surname", is("Orwell")));
        }

        @Test
        @DisplayName("Should return 404 when author not found")
        void getAuthorById_ShouldReturn404WhenNotFound() throws Exception {
            when(authorService.getAuthorById(999L))
                    .thenThrow(new ResourceNotFoundException("Author", "id", 999L));

            mockMvc.perform(get("/authors/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /authors Tests")
    class CreateAuthorTests {

        @Test
        @DisplayName("Should create author and return 201")
        void createAuthor_ShouldReturnCreated() throws Exception {
            AuthorRequest request = AuthorRequest.builder()
                    .name("George")
                    .surname("Orwell")
                    .birthYear(1903)
                    .build();

            when(authorService.createAuthor(any(AuthorRequest.class))).thenReturn(authorResponse);

            mockMvc.perform(post("/authors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("George")));
        }

        @Test
        @DisplayName("Should return 400 for invalid input")
        void createAuthor_ShouldReturn400ForInvalidInput() throws Exception {
            AuthorRequest invalidRequest = AuthorRequest.builder()
                    .name("")
                    .surname("")
                    .birthYear(null)
                    .build();

            mockMvc.perform(post("/authors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /authors/{id} Tests")
    class UpdateAuthorTests {

        @Test
        @DisplayName("Should update author and return 200")
        void updateAuthor_ShouldReturnUpdated() throws Exception {
            AuthorRequest updateRequest = AuthorRequest.builder()
                    .name("Eric")
                    .surname("Blair")
                    .birthYear(1903)
                    .build();

            AuthorResponse updatedResponse = AuthorResponse.builder()
                    .id(1L)
                    .name("Eric")
                    .surname("Blair")
                    .fullName("Eric Blair")
                    .birthYear(1903)
                    .build();

            when(authorService.updateAuthor(eq(1L), any(AuthorRequest.class))).thenReturn(updatedResponse);

            mockMvc.perform(put("/authors/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Eric")));
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent author")
        void updateAuthor_ShouldReturn404WhenNotFound() throws Exception {
            AuthorRequest updateRequest = AuthorRequest.builder()
                    .name("Eric")
                    .surname("Blair")
                    .birthYear(1903)
                    .build();

            when(authorService.updateAuthor(eq(999L), any(AuthorRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Author", "id", 999L));

            mockMvc.perform(put("/authors/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /authors/{id} Tests")
    class DeleteAuthorTests {

        @Test
        @DisplayName("Should delete author and return 204")
        void deleteAuthor_ShouldReturn204() throws Exception {
            doNothing().when(authorService).deleteAuthor(1L);

            mockMvc.perform(delete("/authors/1"))
                    .andExpect(status().isNoContent());

            verify(authorService).deleteAuthor(1L);
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent author")
        void deleteAuthor_ShouldReturn404WhenNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Author", "id", 999L))
                    .when(authorService).deleteAuthor(999L);

            mockMvc.perform(delete("/authors/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when author has books")
        void deleteAuthor_ShouldReturn409WhenAuthorHasBooks() throws Exception {
            doThrow(new AuthorHasBooksException(1L))
                    .when(authorService).deleteAuthor(1L);

            mockMvc.perform(delete("/authors/1"))
                    .andExpect(status().isConflict());
        }
    }
}

