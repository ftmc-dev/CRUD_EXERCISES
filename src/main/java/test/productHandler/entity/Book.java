package test.productHandler.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter

public class Book {
    private static UUID idGenerated = UUID.randomUUID();
    private final UUID bookId;
    private String title;
    private String author;
    private int publishedYear;
    private BookGenre genre;
    private boolean availability;

    public Book() {
        this.bookId = idGenerated;
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.genre = genre;
        this.availability = availability;
    }
}
