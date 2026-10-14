package test.productHandler.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import test.productHandler.dto.GenreDTO;
import test.productHandler.entity.Book;
import test.productHandler.entity.BookGenre;
import test.productHandler.exceptions.BookNotFoundException;
import test.productHandler.repository.BookRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public UUID createBook(Book book) {
        bookRepository.save(book);
        return book.getBookId();
    }

    public void deleteById(UUID bookId) {
        Optional<Book> book = bookRepository.existById(bookId);
        if (book.isEmpty()) {
            throw new BookNotFoundException("Book with id " + bookId + " not found");
        }
        bookRepository.deleteById(bookId);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getById(UUID bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + bookId + " not found"));
    }

    public void updateBook(Book book) {
        if (bookRepository.findById(book.getBookId()).isPresent()) {
            bookRepository.save(book);
        }

        throw new BookNotFoundException("Book with id " + book.getBookId() + " not found");
    }

    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

        public List<Book> searchByAuthorGenreAvailability(String author, BookGenre genre, Boolean availability) {
       return bookRepository.findByAuthorOrGenderOrAvailability(author, genre, availability);
    }
}
