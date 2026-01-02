package com.techforall.bookcatalog.service.impl;

import com.techforall.bookcatalog.exception.ResourceNotFoundException;
import com.techforall.bookcatalog.model.dto.request.BookRequest;
import com.techforall.bookcatalog.model.dto.response.BookResponse;
import com.techforall.bookcatalog.model.dto.response.BookSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.PageResponse;
import com.techforall.bookcatalog.model.entity.Author;
import com.techforall.bookcatalog.model.entity.Book;
import com.techforall.bookcatalog.repository.AuthorRepository;
import com.techforall.bookcatalog.repository.BookRepository;
import com.techforall.bookcatalog.service.BookService;
import com.techforall.bookcatalog.utility.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookSummaryResponse> getAllBooks(Pageable pageable) {
        log.debug("Fetching books - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<BookSummaryResponse> page = bookRepository.findAll(pageable)
                .map(entityMapper::toBookSummaryResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        log.debug("Fetching book with id: {}", id);
        Book book = bookRepository.findByIdWithAuthors(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        return entityMapper.toBookResponse(book);
    }

    @Override
    public BookResponse createBook(BookRequest request) {
        log.debug("Creating new book: {}", request.getTitle());

        Set<Long> authorIds = request.getAuthorIds();
        Set<Author> authors = fetchAuthors(authorIds);

        if (authorIds.isEmpty()) {
            log.info("No authors provided, creating book without authors.");
        }

        else if (authorIds.size() == 1 && authors.isEmpty()) {
            throw new ResourceNotFoundException("Author", "id", authorIds.iterator().next());
        }

        else if (authorIds.size() > 1 && authors.isEmpty()) {
            throw new ResourceNotFoundException("Author", "ids", authorIds);
        }

        Book book = entityMapper.toBookEntity(request);
        book.setAuthors(authors);

        Book savedBook = bookRepository.save(book);
        log.info("Created book with id: {}", savedBook.getId());
        return entityMapper.toBookResponse(savedBook);
    }


    @Override
    public BookResponse updateBook(Long id, BookRequest request) {
        log.debug("Updating book with id: {}", id);

        Book existingBook = bookRepository.findByIdWithAuthors(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        entityMapper.updateBookFromRequest(existingBook, request);

        Set<Long> authorIds = request.getAuthorIds();


        if (authorIds.isEmpty()) {
            log.info("Empty author list provided. Clearing authors.");
            existingBook.setAuthors(new HashSet<>());
        }
        else {

            Set<Author> authors = fetchAuthors(authorIds);

            if (authorIds.size() == 1 && authors.isEmpty()) {
                Long invalidId = authorIds.iterator().next();
                throw new ResourceNotFoundException("Author", "id", invalidId);
            }

            if (authorIds.size() > 1 && authors.isEmpty()) {
                throw new ResourceNotFoundException("Author", "ids", authorIds);
            }

            existingBook.setAuthors(authors);
        }

        Book savedBook = bookRepository.save(existingBook);
        log.info("Updated book with id: {}", id);

        return entityMapper.toBookResponse(savedBook);
    }


    @Override
    public void deleteBook(Long id) {
        log.debug("Deleting book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        bookRepository.delete(book);
        log.info("Deleted book with id: {}", id);
    }

    private Set<Author> fetchAuthors(Set<Long> authorIds) {
        Set<Author> authors = new HashSet<>();

        for (Long authorId : authorIds) {
            authorRepository.findById(authorId)
                    .ifPresentOrElse(
                            authors::add,
                            () -> log.warn("Author with ID {} not found, skipping...", authorId)
                    );
        }

        return authors;
    }

}

