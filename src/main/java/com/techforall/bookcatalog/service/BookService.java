package com.techforall.bookcatalog.service;

import com.techforall.bookcatalog.model.dto.request.BookRequest;
import com.techforall.bookcatalog.model.dto.response.BookResponse;
import com.techforall.bookcatalog.model.dto.response.BookSummaryResponse;
import com.techforall.bookcatalog.model.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface BookService {


    PageResponse<BookSummaryResponse> getAllBooks(Pageable pageable);

    BookResponse getBookById(Long id);

    BookResponse createBook(BookRequest request);

    BookResponse updateBook(Long id, BookRequest request);

    void deleteBook(Long id);
}

