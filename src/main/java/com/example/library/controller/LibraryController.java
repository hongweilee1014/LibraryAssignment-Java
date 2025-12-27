package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.model.Borrower;
import com.example.library.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/borrowers")
    public ResponseEntity<?> registerBorrower(@Valid @RequestBody Borrower borrower) {
        try {
            return new ResponseEntity<>(libraryService.registerBorrower(borrower), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/books")
    public ResponseEntity<?> registerBook(@Valid @RequestBody Book book) {
        try {
            return new ResponseEntity<>(libraryService.registerBook(book), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/books")
    public List<Book> getBooks() {
        return libraryService.getAllBooks();
    }

    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestParam Long borrowerId, @RequestParam Long bookId) {
        try {
            libraryService.borrowBook(borrowerId, bookId);
            return ResponseEntity.ok("Book borrowed successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/return/{bookId}")
    public ResponseEntity<?> returnBook(@PathVariable Long bookId) {
        try {
            libraryService.returnBook(bookId);
            return ResponseEntity.ok("Book returned.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
