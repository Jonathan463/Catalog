package com.techforall.bookcatalog.service;

import com.techforall.bookcatalog.exception.AuthorHasBooksException;
import com.techforall.bookcatalog.exception.ResourceNotFoundException;
import com.techforall.bookcatalog.model.dto.request.AuthorRequest;
import com.techforall.bookcatalog.model.dto.response.AuthorResponse;
import com.techforall.bookcatalog.model.dto.response.AuthorSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.PageResponse;
import com.techforall.bookcatalog.model.entity.Author;
import com.techforall.bookcatalog.repository.AuthorRepository;
import com.techforall.bookcatalog.service.impl.AuthorServiceImpl;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorService Mockito Tests")
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author;
    private AuthorResponse authorResponse;
    private AuthorSummaryResponse authorSummaryResponse;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("George");
        author.setSurname("Orwell");
        author.setBirthYear(1903);
        author.setBooks(new HashSet<>());

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
    @DisplayName("getAllAuthors Tests")
    class GetAllAuthorsTests {

        @Test
        @DisplayName("Should return paginated list of author summaries")
        void getAllAuthors_ShouldReturnAuthorSummaries() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Author> authorPage = new PageImpl<>(List.of(author), pageable, 1);

            when(authorRepository.findAll(pageable)).thenReturn(authorPage);
            when(entityMapper.toAuthorSummaryResponse(author)).thenReturn(authorSummaryResponse);

            PageResponse<AuthorSummaryResponse> result = authorService.getAllAuthors(pageable);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals("George Orwell", result.getContent().get(0).getFullName());
            assertEquals(0, result.getPageNumber());
            assertEquals(1, result.getTotalElements());
            verify(authorRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Should return empty page when no authors exist")
        void getAllAuthors_ShouldReturnEmptyPageWhenNoAuthors() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<Author> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(authorRepository.findAll(pageable)).thenReturn(emptyPage);

            PageResponse<AuthorSummaryResponse> result = authorService.getAllAuthors(pageable);

            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("getAuthorById Tests")
    class GetAuthorByIdTests {

        @Test
        @DisplayName("Should return author when found")
        void getAuthorById_ShouldReturnAuthor() {
            when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
            when(entityMapper.toAuthorResponse(author)).thenReturn(authorResponse);

            AuthorResponse result = authorService.getAuthorById(1L);

            assertNotNull(result);
            assertEquals("George", result.getName());
            verify(authorRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when author not found")
        void getAuthorById_ShouldThrowExceptionWhenNotFound() {
            when(authorRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> authorService.getAuthorById(999L));
        }
    }

    @Nested
    @DisplayName("createAuthor Tests")
    class CreateAuthorTests {

        @Test
        @DisplayName("Should create and return new author")
        void createAuthor_ShouldCreateAndReturnAuthor() {
            AuthorRequest request = AuthorRequest.builder()
                    .name("George")
                    .surname("Orwell")
                    .birthYear(1903)
                    .build();

            Author newAuthor = new Author();
            newAuthor.setName("George");
            newAuthor.setSurname("Orwell");
            newAuthor.setBirthYear(1903);

            when(entityMapper.toAuthorEntity(request)).thenReturn(newAuthor);
            when(authorRepository.save(any(Author.class))).thenReturn(author);
            when(entityMapper.toAuthorResponse(author)).thenReturn(authorResponse);

            AuthorResponse result = authorService.createAuthor(request);

            assertNotNull(result);
            assertEquals("George", result.getName());
            verify(authorRepository).save(any(Author.class));
        }
    }

    @Nested
    @DisplayName("updateAuthor Tests")
    class UpdateAuthorTests {

        @Test
        @DisplayName("Should update existing author")
        void updateAuthor_ShouldUpdateAuthor() {
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

            when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
            when(authorRepository.save(author)).thenReturn(author);
            when(entityMapper.toAuthorResponse(author)).thenReturn(updatedResponse);

            AuthorResponse result = authorService.updateAuthor(1L, updateRequest);

            assertNotNull(result);
            verify(entityMapper).updateAuthorFromRequest(author, updateRequest);
            verify(authorRepository).save(author);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existent author")
        void updateAuthor_ShouldThrowExceptionWhenNotFound() {
            AuthorRequest updateRequest = AuthorRequest.builder()
                    .name("Eric")
                    .surname("Blair")
                    .birthYear(1903)
                    .build();

            when(authorRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> authorService.updateAuthor(999L, updateRequest));
        }
    }

    @Nested
    @DisplayName("deleteAuthor Tests")
    class DeleteAuthorTests {

        @Test
        @DisplayName("Should delete author with no books")
        void deleteAuthor_ShouldDeleteAuthorWithNoBooks() {
            when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
            when(authorRepository.hasBooks(1L)).thenReturn(false);

            assertDoesNotThrow(() -> authorService.deleteAuthor(1L));
            verify(authorRepository).delete(author);
        }

        @Test
        @DisplayName("Should throw AuthorHasBooksException when author has books")
        void deleteAuthor_ShouldThrowExceptionWhenAuthorHasBooks() {
            when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
            when(authorRepository.hasBooks(1L)).thenReturn(true);

            assertThrows(AuthorHasBooksException.class,
                    () -> authorService.deleteAuthor(1L));
            verify(authorRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent author")
        void deleteAuthor_ShouldThrowExceptionWhenNotFound() {
            when(authorRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> authorService.deleteAuthor(999L));
        }
    }
}

