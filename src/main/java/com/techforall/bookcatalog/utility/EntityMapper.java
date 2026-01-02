package com.techforall.bookcatalog.utility;

import com.techforall.bookcatalog.model.dto.request.AuthorRequest;
import com.techforall.bookcatalog.model.dto.request.BookRequest;
import com.techforall.bookcatalog.model.dto.response.AuthorResponse;
import com.techforall.bookcatalog.model.dto.response.AuthorSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.BookResponse;
import com.techforall.bookcatalog.model.dto.response.BookSummaryResponse;
import com.techforall.bookcatalog.model.entity.Author;
import com.techforall.bookcatalog.model.entity.Book;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Component
public class EntityMapper {


    public AuthorResponse toAuthorResponse(Author author) {
        if (author == null) {
            return null;
        }
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .surname(author.getSurname())
                .fullName(author.getFullName())
                .birthYear(author.getBirthYear())
                .build();
    }


    public AuthorSummaryResponse toAuthorSummaryResponse(Author author) {
        if (author == null) {
            return null;
        }
        return AuthorSummaryResponse.builder()
                .id(author.getId())
                .fullName(author.getFullName())
                .build();
    }


    public Author toAuthorEntity(AuthorRequest request) {
        if (request == null) {
            return null;
        }
        Author author = new Author();
        author.setName(request.getName());
        author.setSurname(request.getSurname());
        author.setBirthYear(request.getBirthYear());
        return author;
    }


    public void updateAuthorFromRequest(Author author, AuthorRequest request) {
        if (request.getName() != null) {
            author.setName(request.getName());
        }
        if (request.getSurname() != null) {
            author.setSurname(request.getSurname());
        }
        if (request.getBirthYear() != null) {
            author.setBirthYear(request.getBirthYear());
        }
    }


    public BookResponse toBookResponse(Book book) {
        if (book == null) {
            return null;
        }
        Set<AuthorSummaryResponse> authorSummaries = book.getAuthors().stream()
                .map(this::toAuthorSummaryResponse)
                .collect(Collectors.toSet());

        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authors(authorSummaries)
                .publisher(book.getPublisher())
                .edition(book.getEdition())
                .publishedDate(book.getPublishedDate())
                .build();
    }

    /**
     * Maps a Book entity to BookSummaryResponse.
     *
     * @param book The book entity
     * @return BookSummaryResponse for list usage
     */
    public BookSummaryResponse toBookSummaryResponse(Book book) {
        if (book == null) {
            return null;
        }
        return BookSummaryResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .publisher(book.getPublisher())
                .build();
    }


    public Book toBookEntity(BookRequest request) {
        if (request == null) {
            return null;
        }
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setPublisher(request.getPublisher());
        book.setEdition(request.getEdition());
        book.setPublishedDate(request.getPublishedDate());
        return book;
    }


    public void updateBookFromRequest(Book book, BookRequest request) {
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getPublisher() != null) {
            book.setPublisher(request.getPublisher());
        }
        if (request.getEdition() != null) {
            book.setEdition(request.getEdition());
        }
        if (request.getPublishedDate() != null) {
            book.setPublishedDate(request.getPublishedDate());
        }
    }
}

