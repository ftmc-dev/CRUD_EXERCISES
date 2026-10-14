package test.productHandler.mappers;

import org.mapstruct.Mapper;
import test.productHandler.dto.BookCreateDTO;
import test.productHandler.dto.BookDTO;
import test.productHandler.dto.GenreDTO;
import test.productHandler.dto.UpdateBookRequestDTO;
import test.productHandler.entity.Book;
import test.productHandler.entity.BookGenre;

@Mapper(componentModel = "spring")
public interface BookMapper {

    default Book fromBookCreateDTOToBook(BookCreateDTO bookCreateDTO) {
        return new Book();
    }

    default BookDTO fromBookToBookDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setBookId(book.getBookId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setPublishedYear(book.getPublishedYear());
        bookDTO.setGenre(GenreDTO.valueOf(book.getGenre().name()));   // same shape as Product
        bookDTO.setAvailability(book.isAvailability());
        return bookDTO;
    }

    default Book fromUpdateBookRequestDTOToBook(UpdateBookRequestDTO updateBookRequestDTO) {
        Book book = new Book();
        book.setTitle(updateBookRequestDTO.getTitle());
        book.setAuthor(updateBookRequestDTO.getAuthor());
        book.setPublishedYear(updateBookRequestDTO.getPublishedYear());
        book.setGenre(mapGenre(updateBookRequestDTO.getGenre()));
        book.setAvailability(updateBookRequestDTO.getAvailability());

        return book;
    }

    default BookGenre mapGenre(GenreDTO genreDTO) {
        if (genreDTO == null) {
            return null;
        }
        return BookGenre.valueOf(genreDTO.name());
    }

    default GenreDTO mapGenre(BookGenre genre) {
        if (genre == null) {
            return null;
        }
        return GenreDTO.valueOf(genre.name());
    }
}