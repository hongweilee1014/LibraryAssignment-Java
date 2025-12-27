package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.Borrower;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Initializes Mockito mocks
class LibraryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private LibraryService libraryService;

    private Borrower borrower;
    private Book book;

    @BeforeEach
    void setUp() {
        // Common setup for tests
        borrower = new Borrower();
        borrower.setId(1L);
        borrower.setName("Wei Lee");
        borrower.setEmail("wei@test.com");

        book = new Book();
        book.setId(1L);
        book.setIsbn("123-456");
        book.setTitle("Clean Code");
        book.setAuthor("Uncle Bob");
    }

    // --- BORROWER TESTS ---

    @Test
    void registerBorrower_ShouldSuccess_WhenEmailIsUnique() {
        // Arrange
        when(borrowerRepository.existsByEmail(anyString())).thenReturn(false);
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(borrower);

        // Act
        Borrower result = libraryService.registerBorrower(borrower);

        // Assert
        assertNotNull(result);
        assertEquals("wei@test.com", result.getEmail());
        verify(borrowerRepository, times(1)).save(borrower);
    }

    @Test
    void registerBorrower_ShouldThrow_WhenEmailExists() {
        // Arrange
        when(borrowerRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            libraryService.registerBorrower(borrower);
        });

        assertEquals("Email already registered.", exception.getMessage());
        verify(borrowerRepository, never()).save(any());
    }

    // --- BOOK REGISTRATION TESTS (ISBN LOGIC) ---

    @Test
    void registerBook_ShouldSuccess_WhenIsbnIsNew() {
        // Arrange
        when(bookRepository.findFirstByIsbn(anyString())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book result = libraryService.registerBook(book);

        // Assert
        assertNotNull(result);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void registerBook_ShouldSuccess_WhenIsbnExists_AndMetadataMatches() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setIsbn("123-456");
        existingBook.setTitle("Clean Code"); // Matches
        existingBook.setAuthor("Uncle Bob"); // Matches

        when(bookRepository.findFirstByIsbn("123-456")).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        libraryService.registerBook(book);

        // Assert
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void registerBook_ShouldThrow_WhenIsbnExists_ButTitleDiffers() {
        // Arrange
        Book existingBook = new Book();
        existingBook.setIsbn("123-456");
        existingBook.setTitle("Dirty Code"); // DIFFERENT Title
        existingBook.setAuthor("Uncle Bob");

        when(bookRepository.findFirstByIsbn("123-456")).thenReturn(Optional.of(existingBook));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            libraryService.registerBook(book);
        });

        assertEquals("ISBN conflict: Metadata mismatch.", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    // --- BORROWING TESTS ---

    @Test
    void borrowBook_ShouldSuccess_WhenBookAvailable() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));

        // Act
        libraryService.borrowBook(1L, 1L);

        // Assert
        assertEquals(1L, book.getCurrentBorrowerId());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void borrowBook_ShouldThrow_WhenBookAlreadyBorrowed() {
        // Arrange
        book.setCurrentBorrowerId(99L); // Already borrowed by someone else
        
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            libraryService.borrowBook(1L, 1L);
        });

        assertEquals("Book is already borrowed.", exception.getMessage());
    }

    @Test
    void borrowBook_ShouldThrow_WhenBookNotFound() {
        // Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            libraryService.borrowBook(1L, 99L);
        });
    }

    // --- RETURN TESTS ---

    @Test
    void returnBook_ShouldSuccess_WhenBookExists() {
        // Arrange
        book.setCurrentBorrowerId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        libraryService.returnBook(1L);

        // Assert
        assertNull(book.getCurrentBorrowerId());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void getAllBooks_ShouldReturnList() {
        // Arrange
        List<Book> books = new ArrayList<>();
        books.add(book);
        when(bookRepository.findAll()).thenReturn(books);

        // Act
        List<Book> result = libraryService.getAllBooks();

        // Assert
        assertEquals(1, result.size());
    }
}
