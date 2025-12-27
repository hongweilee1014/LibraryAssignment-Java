package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.Borrower;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowerRepository;
import lombok.extern.slf4j.Slf4j; // <--- This enables Logging
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j // <--- Automatically creates a 'log' object for this class
public class LibraryService {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;

    public LibraryService(BookRepository bookRepository, BorrowerRepository borrowerRepository) {
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
    }

    @Transactional
    public Borrower registerBorrower(Borrower borrower) {
        log.info("Request: Registering Borrower with email {}", borrower.getEmail());

        try {
            if (borrowerRepository.existsByEmail(borrower.getEmail())) {
                throw new IllegalStateException("Email already registered.");
            }
            Borrower savedBorrower = borrowerRepository.save(borrower);
            
            log.info("Response: Successfully registered Borrower ID {}", savedBorrower.getId());
            return savedBorrower;

        } catch (Exception ex) {
            log.error("Exception: Failed to register borrower {}. Error: {}", borrower.getEmail(), ex.getMessage());
            throw ex; // Re-throw so Controller handles the HTTP status
        }
    }

    @Transactional
    public Book registerBook(Book book) {
        log.info("Request: Registering Book ISBN {}", book.getIsbn());

        try {
            // Rule: If ISBN exists, Title/Author must match
            bookRepository.findFirstByIsbn(book.getIsbn()).ifPresent(existing -> {
                if (!existing.getTitle().equals(book.getTitle()) || 
                    !existing.getAuthor().equals(book.getAuthor())) {
                    throw new IllegalStateException("ISBN conflict: Metadata mismatch.");
                }
            });

            Book savedBook = bookRepository.save(book);
            log.info("Response: Successfully registered Book ID {}", savedBook.getId());
            return savedBook;

        } catch (Exception ex) {
            log.error("Exception: Failed to register book {}. Error: {}", book.getTitle(), ex.getMessage());
            throw ex;
        }
    }

    @Transactional
    public void borrowBook(Long borrowerId, Long bookId) {
        log.info("Request: Borrower {} borrowing Book {}", borrowerId, bookId);

        try {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found"));
            
            Borrower borrower = borrowerRepository.findById(borrowerId)
                    .orElseThrow(() -> new IllegalArgumentException("Borrower not found"));

            if (book.getCurrentBorrowerId() != null) {
                throw new IllegalStateException("Book is already borrowed.");
            }

            book.setCurrentBorrowerId(borrower.getId());
            bookRepository.save(book);

            log.info("Response: Successfully borrowed Book {}", bookId);

        } catch (Exception ex) {
            log.error("Exception: Borrow transaction failed for Book {}. Error: {}", bookId, ex.getMessage());
            throw ex;
        }
    }

    @Transactional
    public void returnBook(Long bookId) {
        log.info("Request: Returning Book {}", bookId);

        try {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found"));
            
            book.setCurrentBorrowerId(null);
            bookRepository.save(book);

            log.info("Response: Successfully returned Book {}", bookId);

        } catch (Exception ex) {
            log.error("Exception: Return transaction failed for Book {}. Error: {}", bookId, ex.getMessage());
            throw ex;
        }
    }

    public List<Book> getAllBooks() {
        log.info("Request: Get all books");
        List<Book> books = bookRepository.findAll();
        log.info("Response: Retrieved {} books", books.size());
        return books;
    }
}
