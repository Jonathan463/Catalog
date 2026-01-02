package com.techforall.bookcatalog.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private Long id;
    private String title;
    private Set<AuthorSummaryResponse> authors;
    private String publisher;
    private String edition;
    private LocalDate publishedDate;
}

