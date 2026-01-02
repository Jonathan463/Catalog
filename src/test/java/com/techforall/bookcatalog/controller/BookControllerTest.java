package com.techforall.bookcatalog.controller;

import com.techforall.bookcatalog.exception.ResourceNotFoundException;
import com.techforall.bookcatalog.model.dto.request.BookRequest;
import com.techforall.bookcatalog.model.dto.response.AuthorSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.BookResponse;
import com.techforall.bookcatalog.model.dto.response.BookSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.PageResponse;
import com.techforall.bookcatalog.service.BookService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookController.class)
@DisplayName("BookController MockMvc Tests")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    private BookResponse bookResponse;
    private BookSummaryResponse bookSummaryResponse;

    @BeforeEach
    void setUp() {
        Set<AuthorSummaryResponse> authors = Set.of(
                AuthorSummaryResponse.builder().id(1L).fullName("George Orwell").build());

        bookResponse = BookResponse.builder()
                .id(1L)
                .title("1984")
                .authors(authors)
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
    @DisplayName("GET /books Tests")
    class GetAllBooksTests {

        @Test
        @DisplayName("Should return paginated list of books")
        void getAllBooks_ShouldReturnBookList() throws Exception {
            PageResponse<BookSummaryResponse> pageResponse = PageResponse.<BookSummaryResponse>builder()
                    .content(List.of(bookSummaryResponse))
                    .pageNumber(0)
                    .pageSize(20)
                    .totalElements(1)
                    .totalPages(1)
                    .first(true)
                    .last(true)
                    .build();

            when(bookService.getAllBooks(any(Pageable.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/books"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title", is("1984")))
                    .andExpect(jsonPath("$.totalElements", is(1)));
        }

        @Test
        @DisplayName("Should return empty page when no books")
        void getAllBooks_ShouldReturnEmptyPage() throws Exception {
            PageResponse<BookSummaryResponse> emptyPage = PageResponse.<BookSummaryResponse>builder()
                    .content(List.of())
                    .pageNumber(0)
                    .pageSize(20)
                    .totalElements(0)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .build();

            when(bookService.getAllBooks(any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)));
        }
    }

    @Nested
    @DisplayName("GET /books/{id} Tests")
    class GetBookByIdTests {

        @Test
        @DisplayName("Should return book when found")
        void getBookById_ShouldReturnBook() throws Exception {
            when(bookService.getBookById(1L)).thenReturn(bookResponse);

            mockMvc.perform(get("/books/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.title", is("1984")))
                    .andExpect(jsonPath("$.publisher", is("Secker & Warburg")));
        }

        @Test
        @DisplayName("Should return 404 when book not found")
        void getBookById_ShouldReturn404WhenNotFound() throws Exception {
            when(bookService.getBookById(999L))
                    .thenThrow(new ResourceNotFoundException("Book", "id", 999L));

            mockMvc.perform(get("/books/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /books Tests")
    class CreateBookTests {

        @Test
        @DisplayName("Should create book and return 201")
        void createBook_ShouldReturnCreated() throws Exception {
            BookRequest request = BookRequest.builder()
                    .title("1984")
                    .authorIds(Set.of(1L))
                    .publisher("Secker & Warburg")
                    .edition("First Edition")
                    .publishedDate(LocalDate.of(1949, 6, 8))
                    .build();

            when(bookService.createBook(any(BookRequest.class))).thenReturn(bookResponse);

            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.title", is("1984")));
        }

        @Test
        @DisplayName("Should return 400 for invalid input")
        void createBook_ShouldReturn400ForInvalidInput() throws Exception {
            BookRequest invalidRequest = BookRequest.builder()
                    .title("")
                    .authorIds(Set.of())
                    .publisher("")
                    .build();

            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /books/{id} Tests")
    class UpdateBookTests {

        @Test
        @DisplayName("Should update book and return 200")
        void updateBook_ShouldReturnUpdated() throws Exception {
            BookRequest updateRequest = BookRequest.builder()
                    .title("Nineteen Eighty-Four")
                    .authorIds(Set.of(1L))
                    .publisher("Penguin")
                    .edition("New Edition")
                    .publishedDate(LocalDate.of(1950, 1, 1))
                    .build();

            when(bookService.updateBook(eq(1L), any(BookRequest.class))).thenReturn(bookResponse);

            mockMvc.perform(put("/books/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent book")
        void updateBook_ShouldReturn404WhenNotFound() throws Exception {
            BookRequest updateRequest = BookRequest.builder()
                    .title("Updated Title")
                    .authorIds(Set.of(1L))
                    .publisher("Publisher")
                    .edition("Edition")
                    .publishedDate(LocalDate.now())
                    .build();

            when(bookService.updateBook(eq(999L), any(BookRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Book", "id", 999L));

            mockMvc.perform(put("/books/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /books/{id} Tests")
    class DeleteBookTests {

        @Test
        @DisplayName("Should delete book and return 204")
        void deleteBook_ShouldReturn204() throws Exception {
            doNothing().when(bookService).deleteBook(1L);

            mockMvc.perform(delete("/books/1"))
                    .andExpect(status().isNoContent());

            verify(bookService).deleteBook(1L);
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent book")
        void deleteBook_ShouldReturn404WhenNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Book", "id", 999L))
                    .when(bookService).deleteBook(999L);

            mockMvc.perform(delete("/books/999"))
                    .andExpect(status().isNotFound());
        }
    }
}

