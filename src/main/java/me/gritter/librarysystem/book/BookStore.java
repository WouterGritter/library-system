package me.gritter.librarysystem.book;

import me.gritter.librarysystem.Genre;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableCollection;

public class BookStore {

    private final Collection<BookRecord> bookRecords = new HashSet<>();

    public void cleanRecords() {
        bookRecords.removeIf(record -> record.getQuantity() == 0);
    }

    public BookRecord getBookRecord(Book book) {
        BookRecord record = bookRecords.stream()
                .filter(r -> r.getBook().equals(book))
                .findAny()
                .orElse(null);

        if (record == null) {
            record = new BookRecord(book);
            bookRecords.add(record);
        }

        return record;
    }

    public Collection<BookRecord> getBookRecords() {
        return unmodifiableCollection(bookRecords);
    }

    public Stream<BookRecord> findBookRecordByTitle(String title) {
        return bookRecords.stream()
                .filter(record -> record.getBook().getTitle().equals(title));
    }

    public Stream<BookRecord> findBookRecordByAuthor(String author) {
        return bookRecords.stream()
                .filter(record -> record.getBook().getAuthor().equals(author));
    }

    public Stream<BookRecord> findBookRecordByIsbn(String isbn) {
        return bookRecords.stream()
                .filter(record -> record.getBook().getIsbn().equals(isbn));
    }

    public Stream<BookRecord> findBookRecordByGenre(Genre genre) {
        return bookRecords.stream()
                .filter(record -> record.getBook().getGenre().equals(genre));
    }
}
