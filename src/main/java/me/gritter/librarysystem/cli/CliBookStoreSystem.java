package me.gritter.librarysystem.cli;

import me.gritter.librarysystem.Genre;
import me.gritter.librarysystem.book.Book;
import me.gritter.librarysystem.book.BookRecord;
import me.gritter.librarysystem.book.BookStore;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.copyOfRange;
import static me.gritter.librarysystem.Utils.parseInt;

public class CliBookStoreSystem implements CliSystem {

    private final BookStore bookStore;

    public CliBookStoreSystem(BookStore bookStore) {
        this.bookStore = bookStore;
    }

    @Override
    public boolean parseCommand(String[] parts) {
        if (parts.length < 1) {
            return false;
        }

        String[] commandParts = copyOfRange(parts, 1, parts.length);

        switch (parts[0]) {
            case "l":
                listBooksCommand();
                return true;
            case "s":
                searchBookCommand(commandParts);
                return true;
            case "a":
                addBookCommand(commandParts);
                return true;
            case "q":
                modifyBookQuantityCommand(commandParts);
                return true;
            case "d":
                deleteBookCommand(commandParts);
                return true;
            case "g":
                listGenresCommand();
                return true;
            default:
                return false;
        }
    }

    private Stream<BookRecord> findBookRecord(String fieldName, String value) {
        switch (fieldName) {
            case "title":
                return bookStore.findBookRecordByTitle(value);
            case "author":
                return bookStore.findBookRecordByAuthor(value);
            case "isbn":
                return bookStore.findBookRecordByIsbn(value);
            case "genre":
                return Genre.parseGenre(value)
                        .map(bookStore::findBookRecordByGenre)
                        .orElseGet(Stream::empty);
            default:
                return Stream.empty();
        }
    }

    private Optional<BookRecord> findSingleBookRecord(String fieldName, String value) {
        List<BookRecord> records = findBookRecord(fieldName, value).collect(Collectors.toList());

        if (records.isEmpty()) {
            System.out.println("Found no books matching your criteria.");
            return Optional.empty();
        }

        if (records.size() > 1) {
            System.out.println("Found multiple books matching your criteria. Please use a more specific field, or provide all parameters using the 'a' command.");
            return Optional.empty();
        }

        return Optional.of(records.get(0));
    }

    private void listBooksCommand() {
        System.out.println("Available books:");
        for (BookRecord record : bookStore.getBookRecords()) {
            System.out.println();
            System.out.println(record);
        }
    }

    private void searchBookCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Invalid arguments.");
            return;
        }

        BookRecord record = findSingleBookRecord(parts[0], parts[1]).orElse(null);
        if (record == null) {
            return; // Message has been sent by findSingleBookRecord().
        }

        System.out.println(record);
    }

    private void addBookCommand(String[] parts) {
        if (parts.length < 4) {
            System.out.println("Invalid arguments.");
            return;
        }

        String title = parts[0];
        String author = parts[1];
        String isbn = parts[2];
        Genre genre = Genre.parseGenre(parts[3]).orElse(null);

        if (genre == null) {
            System.out.println("Invalid genre. Type 'g' to list all genres.");
            return;
        }

        int quantity = 1;
        if (parts.length >= 5) {
            quantity = parseInt(parts[4]).orElse(-1);
            if (quantity <= 0) {
                System.out.println("Invalid quantity.");
                return;
            }
        }

        Book book = new Book(title, author, isbn, genre);
        BookRecord record = bookStore.getBookRecord(book);
        record.updateQuantity(quantity);

        System.out.println("Quantity of book '" + book.getTitle() + "' is now " + record.getQuantity() + ".");
    }

    private void modifyBookQuantityCommand(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Invalid arguments.");
            return;
        }

        BookRecord record = findSingleBookRecord(parts[0], parts[1]).orElse(null);
        if (record == null) {
            return; // Message has been sent by findSingleBookRecord()
        }

        int quantityDelta = parseInt(parts[2]).orElse(0);
        if (quantityDelta == 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        record.updateQuantity(quantityDelta);
        bookStore.cleanRecords();

        System.out.println("Quantity of book '" + record.getBook().getTitle() + "' is now " + record.getQuantity() + ".");
    }

    private void deleteBookCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Invalid arguments.");
            return;
        }

        BookRecord record = findSingleBookRecord(parts[0], parts[1]).orElse(null);
        if (record == null) {
            return; // Message has been sent by findSingleBookRecord().
        }

        record.setQuantity(0);
        bookStore.cleanRecords();

        System.out.println("Book '" + record.getBook().getTitle() + "' has been removed from the library.");
    }

    private void listGenresCommand() {
        System.out.println("Available genres (case insensitive):");
        for (Genre genre : Genre.values()) {
            System.out.println(" " + genre.name());
        }
    }
}
