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
    public Borrower registerBorrower(Borrower request) {
        log.info("Attempting to register borrower: {}", request.getEmail());
		
		   // 1. Application-level check (Friendly error)
           if (borrowerRepository.existsByEmail(request.getEmail())) {
                log.warn("Registration rejected. Email '{}' already exists.", request.getEmail());
                throw new IllegalStateException("Email already registered.");
            }

        try {
         

            // 2. Database interaction
            Borrower saved = borrowerRepository.save(request);
            log.info("Borrower registered successfully. ID: {}", saved.getId());
            return saved;

        } catch (IllegalArgumentException e) {
            // Pass through our specific business validation errors
            throw e; 
            
        } catch (Exception e) {
            // 3. Catch unexpected errors (Database down, Network issues, etc.)
            log.error("Unexpected error while registering borrower: {}", e.getMessage(), e);
            throw new RuntimeException("System error: Unable to register borrower. Please try again later.");
        }
    }

    @Transactional
   public Book registerBook(Book request) {
        log.info("Registering book ISBN: {}", request.getIsbn());
		   // 1. Check existing copies for consistency
            List<Book> existingCopies = bookRepository.findByIsbn(request.getIsbn());

            if (!existingCopies.isEmpty()) {
                Book originalMetadata = existingCopies.get(0);

                // Rule: "2 books with the same ISBN numbers must have the same title and same author"
                boolean titleMatch = originalMetadata.getTitle().equalsIgnoreCase(request.getTitle());
                boolean authorMatch = originalMetadata.getAuthor().equalsIgnoreCase(request.getAuthor());

                if (!titleMatch || !authorMatch) {
                    log.warn("ISBN Conflict! Input: '{}' by '{}'. Existing: '{}' by '{}'", 
                              request.getTitle(), request.getAuthor(), 
                              originalMetadata.getTitle(), originalMetadata.getAuthor());
                    
                    throw new IllegalStateException("ISBN conflict: Metadata mismatch.");
                }
                log.info("ISBN matches existing records. Adding a new physical copy.");
            } else {
                log.info("New ISBN detected. Creating first copy.");
            }

        try {
         

            // 2. Create and Save the new physical copy
            Book newCopy = new Book();
            newCopy.setIsbn(request.getIsbn());
            newCopy.setTitle(request.getTitle());
            newCopy.setAuthor(request.getAuthor());
            
            Book savedBook = bookRepository.save(newCopy);
            log.info("Book saved successfully. New ID: {}", savedBook.getId());
            return savedBook;

        } catch (IllegalArgumentException e) {
            // SPECIFIC CATCH: Business rule failed. Re-throw so Controller sends 400 Bad Request.
            throw e;

        } catch (Exception e) {
            // GENERAL CATCH: Database or System failure. Log it and throw generic error.
            log.error("Unexpected error while registering book ISBN {}: {}", request.getIsbn(), e.getMessage(), e);
            throw new RuntimeException("System error: Unable to register book. Please try again later.");
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
