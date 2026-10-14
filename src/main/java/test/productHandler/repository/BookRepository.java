package test.productHandler.repository;

import org.springframework.stereotype.Repository;
import test.productHandler.entity.Book;
import test.productHandler.entity.BookGenre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookRepository {

    private final Map<UUID, Book> bookMap = new HashMap<>();

    public UUID save(Book book) {
        bookMap.put(book.getBookId(), book);
        return book.getBookId();
    }

    public Optional<Book> findById(UUID bookId) {
        return Optional.ofNullable(bookMap.get(bookId));
    }

    public void deleteById(UUID bookId) {
        bookMap.remove(bookId);
    }

    public Optional<Book> existById(UUID bookId) {
        return Optional.ofNullable(bookMap.get(bookId));
    }

    public List<Book> findAll() {
        return new ArrayList<>(bookMap.values());
    }

    public List<Book> findByAuthor(String author) {
        return bookMap.values().stream().filter(book ->
                        book.getAuthor().equals(author)
                )
                .toList();
    }

    public List<Book> findByAuthorOrGender(String author, BookGenre genre) {
        return bookMap.values().stream().filter(book ->
                        book.getAuthor().equals(author) || book.getGenre().equals(genre)
                )
                .toList();
    }

    public List<Book> findByAuthorOrGenderOrAvailability(String author, BookGenre genre, Boolean availability) {
        return bookMap.values().stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author) || book.getGenre().equals(genre) || book.isAvailability())
                .toList();
    }
}
