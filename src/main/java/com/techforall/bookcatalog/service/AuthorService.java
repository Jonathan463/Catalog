package com.techforall.bookcatalog.service;

import com.techforall.bookcatalog.model.dto.request.AuthorRequest;
import com.techforall.bookcatalog.model.dto.response.AuthorResponse;
import com.techforall.bookcatalog.model.dto.response.AuthorSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;


public interface AuthorService {

    PageResponse<AuthorSummaryResponse> getAllAuthors(Pageable pageable);

    AuthorResponse getAuthorById(Long id);

    AuthorResponse createAuthor(AuthorRequest request);

    AuthorResponse updateAuthor(Long id, AuthorRequest request);

    void deleteAuthor(Long id);
}

