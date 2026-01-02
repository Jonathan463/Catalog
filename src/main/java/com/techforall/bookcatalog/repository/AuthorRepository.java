package com.techforall.bookcatalog.repository;

import com.techforall.bookcatalog.model.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {


    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Book b JOIN b.authors a WHERE a.id = :authorId")
    boolean hasBooks(@Param("authorId") Long authorId);
}

