package com.techforall.bookcatalog.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSummaryResponse {

    private Long id;
    private String title;
    private String publisher;
}

