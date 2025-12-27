package com.example.library.repository;

import com.example.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Finds the first book with this ISBN (to check metadata)
    Optional<Book> findFirstByIsbn(String isbn);
}
