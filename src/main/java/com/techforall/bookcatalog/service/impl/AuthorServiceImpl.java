package com.techforall.bookcatalog.service.impl;

import com.techforall.bookcatalog.exception.AuthorHasBooksException;
import com.techforall.bookcatalog.exception.ResourceNotFoundException;
import com.techforall.bookcatalog.model.dto.request.AuthorRequest;
import com.techforall.bookcatalog.model.dto.response.AuthorResponse;
import com.techforall.bookcatalog.model.dto.response.AuthorSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.PageResponse;
import com.techforall.bookcatalog.model.entity.Author;
import com.techforall.bookcatalog.repository.AuthorRepository;
import com.techforall.bookcatalog.service.AuthorService;
import com.techforall.bookcatalog.utility.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuthorSummaryResponse> getAllAuthors(Pageable pageable) {
        log.debug("Fetching authors - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<AuthorSummaryResponse> page = authorRepository.findAll(pageable)
                .map(entityMapper::toAuthorSummaryResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(Long id) {
        log.debug("Fetching author with id: {}", id);
        Author author = findAuthorById(id);
        return entityMapper.toAuthorResponse(author);
    }

    @Override
    public AuthorResponse createAuthor(AuthorRequest request) {
        log.debug("Creating new author: {} {}", request.getName(), request.getSurname());
        Author author = entityMapper.toAuthorEntity(request);
        Author savedAuthor = authorRepository.save(author);
        log.info("Created author with id: {}", savedAuthor.getId());
        return entityMapper.toAuthorResponse(savedAuthor);
    }

    @Override
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        log.debug("Updating author with id: {}", id);
        Author existingAuthor = findAuthorById(id);
        entityMapper.updateAuthorFromRequest(existingAuthor, request);
        Author savedAuthor = authorRepository.save(existingAuthor);
        log.info("Updated author with id: {}", id);
        return entityMapper.toAuthorResponse(savedAuthor);
    }

    @Override
    public void deleteAuthor(Long id) {
        log.debug("Attempting to delete author with id: {}", id);
        Author author = findAuthorById(id);

        if (authorRepository.hasBooks(id)) {
            log.warn("Cannot delete author with id: {} - has associated books", id);
            throw new AuthorHasBooksException(id);
        }

        authorRepository.delete(author);
        log.info("Deleted author with id: {}", id);
    }

    private Author findAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
    }
}

