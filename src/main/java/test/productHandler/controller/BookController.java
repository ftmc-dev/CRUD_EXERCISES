package test.productHandler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import test.productHandler.api.BookApi;
import test.productHandler.dto.BookCreateDTO;
import test.productHandler.dto.BookCreationResponseDTO;
import test.productHandler.dto.BookDTO;
import test.productHandler.dto.GenreDTO;
import test.productHandler.dto.UpdateBookRequestDTO;
import test.productHandler.entity.Book;
import test.productHandler.entity.BookGenre;
import test.productHandler.mappers.BookMapper;
import test.productHandler.services.BookService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BookController implements BookApi {

    private final BookService bookService;
    private final BookMapper bookMapper;


    @Override
    public ResponseEntity<BookCreationResponseDTO> createNewBook(BookCreateDTO bookCreateDTO) {
       Book book = bookMapper.fromBookCreateDTOToBook(bookCreateDTO);
       UUID bookId = bookService.createBook(book);
       return ResponseEntity.status(201).body(new BookCreationResponseDTO(bookId));
    }

    @Override
    public ResponseEntity<Void> deleteBookById(UUID bookId) {
        bookService.deleteById(bookId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<BookDTO>> getAllBooks() {
       List<Book> books = bookService.getAllBooks();
       List<BookDTO> bookDTO = books.stream()
               .map(bookMapper::fromBookToBookDTO)
               .collect(Collectors.toList());
        return ResponseEntity.status(200).body(bookDTO);
    }

    @Override
    public ResponseEntity<BookDTO> getBookById(UUID bookId) {
        Book books = bookService.getById(bookId);
        BookDTO result = bookMapper.fromBookToBookDTO(books);
        return ResponseEntity.status(200).body(result);
    }

    @Override
    public ResponseEntity<List<BookDTO>> searchByAuthorGenreAvailability(String author, GenreDTO genre, Boolean availability) {
        BookGenre bookGenre = bookMapper.mapGenre(genre);
        List<Book> books = bookService.searchByAuthorGenreAvailability(author, bookGenre, availability);
        List<BookDTO>  bookDTO = books.stream()
                .map(bookMapper::fromBookToBookDTO)
                .toList();
        return ResponseEntity.status(200).body(bookDTO);
    }

    @Override
    public ResponseEntity<Void> updateAllBooks(UUID bookId, UpdateBookRequestDTO updateBookRequestDTO) {
        Book book = bookMapper.fromUpdateBookRequestDTOToBook(updateBookRequestDTO);
        bookService.updateBook(book);
        return ResponseEntity.noContent().build();
    }
}
