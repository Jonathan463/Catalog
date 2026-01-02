package com.techforall.bookcatalog.controller;

import com.techforall.bookcatalog.model.dto.request.AuthorRequest;
import com.techforall.bookcatalog.model.dto.response.AuthorResponse;
import com.techforall.bookcatalog.model.dto.response.AuthorSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.PageResponse;
import com.techforall.bookcatalog.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@Slf4j
public class AuthorController {

    private final AuthorService authorService;


    @GetMapping
    public ResponseEntity<PageResponse<AuthorSummaryResponse>> getAllAuthors(
            @PageableDefault(size = 20, sort = "surname") Pageable pageable) {
        log.info("GET /authors - Fetching authors, page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        PageResponse<AuthorSummaryResponse> authors = authorService.getAllAuthors(pageable);
        return ResponseEntity.ok(authors);
    }


    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest request) {
        log.info("POST /authors - Creating new author: {} {}", request.getName(), request.getSurname());
        AuthorResponse createdAuthor = authorService.createAuthor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuthor);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        log.info("GET /authors/{} - Fetching author details", id);
        AuthorResponse author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }


    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest request) {
        log.info("PUT /authors/{} - Updating author", id);
        AuthorResponse updatedAuthor = authorService.updateAuthor(id, request);
        return ResponseEntity.ok(updatedAuthor);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        log.info("DELETE /authors/{} - Deleting author", id);
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}

