package com.techforall.bookcatalog.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotNull
    private Set<Long> authorIds = new HashSet<>();

    @Size(max = 150, message = "Publisher must not exceed 150 characters")
    private String publisher;

    @Size(max = 50, message = "Edition must not exceed 50 characters")
    private String edition;

    @PastOrPresent(message = "Published date cannot be in the future")
    private LocalDate publishedDate;
}

