package com.techforall.bookcatalog.controller;

import com.techforall.bookcatalog.model.dto.request.BookRequest;
import com.techforall.bookcatalog.model.dto.response.BookResponse;
import com.techforall.bookcatalog.model.dto.response.BookSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.PageResponse;
import com.techforall.bookcatalog.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;


    @GetMapping
    public ResponseEntity<PageResponse<BookSummaryResponse>> getAllBooks(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        log.info("GET /books - Fetching books, page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        PageResponse<BookSummaryResponse> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }


    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        log.info("POST /books - Creating new book: {}", request.getTitle());
        BookResponse createdBook = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        log.info("GET /books/{} - Fetching book details", id);
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }


    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        log.info("PUT /books/{} - Updating book", id);
        BookResponse updatedBook = bookService.updateBook(id, request);
        return ResponseEntity.ok(updatedBook);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("DELETE /books/{} - Deleting book", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}

