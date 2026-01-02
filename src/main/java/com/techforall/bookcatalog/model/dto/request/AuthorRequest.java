package com.techforall.bookcatalog.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @NotBlank(message = "Surname is required")
    @Size(min = 1, max = 100, message = "Surname must be between 1 and 100 characters")
    private String surname;

    @Min(value = 1000, message = "Birth year must be at least 1000")
    @Max(value = 2100, message = "Birth year must not exceed 2100")
    private Integer birthYear;
}

