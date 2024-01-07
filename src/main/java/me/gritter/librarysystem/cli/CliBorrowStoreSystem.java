package me.gritter.librarysystem.cli;

import me.gritter.librarysystem.Genre;
import me.gritter.librarysystem.Utils;
import me.gritter.librarysystem.book.BookRecord;
import me.gritter.librarysystem.book.BookStore;
import me.gritter.librarysystem.borrowsystem.BorrowException;
import me.gritter.librarysystem.borrowsystem.BorrowStore;
import me.gritter.librarysystem.borrowsystem.BorrowedBook;
import me.gritter.librarysystem.user.User;
import me.gritter.librarysystem.user.UserStore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.copyOfRange;

public class CliBorrowStoreSystem implements CliSystem {

    private final BorrowStore borrowStore;
    private final UserStore userStore;
    private final BookStore bookStore;

    private User currentLogin = null;

    public CliBorrowStoreSystem(BorrowStore borrowStore) {
        this.borrowStore = borrowStore;
        this.userStore = borrowStore.getUserStore();
        this.bookStore = borrowStore.getBookStore();
    }

    @Override
    public boolean parseCommand(String[] parts) {
        if (parts.length < 1) {
            return false;
        }

        String[] commandParts = copyOfRange(parts, 1, parts.length);

        switch (parts[0]) {
            case "S":
                showAllBorrowedBooks();
                return true;
            case "O":
                showOverdueBooks();
                return true;
            case "l":
                loginCommand(commandParts);
                return true;
            case "s":
                showBorrowedBooks();
                return true;
            case "b":
                borrowBook(commandParts);
                return true;
            case "r":
                returnBook(commandParts);
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

    private void showAllBorrowedBooks() {
        System.out.println("All borrowed books:");
        borrowStore.getBorrowedBooks()
                .forEach(System.out::println);
    }

    private void showOverdueBooks() {
        System.out.println("All overdue books:");
        borrowStore.findOverdueBooks()
                .map(borrowed -> "book=" + borrowed.getBook().getTitle() + ", fine=" + borrowed.calculateOverdueFine())
                .forEach(System.out::println);
    }

    private void loginCommand(String[] parts) {
        if (parts.length < 1) {
            System.out.println("Invalid arguments.");
            return;
        }

        UUID uuid = Utils.parseUUID(parts[0]).orElse(null);
        if (uuid == null) {
            System.out.println("Invalid UUID.");
            return;
        }

        User user = userStore.findUser(uuid).orElse(null);
        if (user == null) {
            System.out.println("No user exists with that UUID.");
            return;
        }

        currentLogin = user;
        System.out.println("You are now logged in as " + user.getName() + ".");
    }

    private void showBorrowedBooks() {
        if (currentLogin == null) {
            System.out.println("Please log in first.");
            return;
        }

        System.out.println("Your borrowed books:");
        borrowStore.findBorrowedBooksByUser(currentLogin)
                .forEach(System.out::println);
    }

    private void borrowBook(String[] parts) {
        if (currentLogin == null) {
            System.out.println("Please log in first.");
            return;
        }

        if (parts.length < 2) {
            System.out.println("Invalid arguments.");
            return;
        }

        BookRecord record = findSingleBookRecord(parts[0], parts[1]).orElse(null);
        if (record == null) {
            return; // Message has been sent by findSingleBookRecord()
        }

        try {
            borrowStore.borrowBook(currentLogin, record.getBook());
            System.out.println("Successfully borrowed book '" + record.getBook().getTitle() + "'!");
        } catch (BorrowException e) {
            System.out.println(e.getMessage());
        }
    }

    private void returnBook(String[] parts) {
        if (currentLogin == null) {
            System.out.println("Please log in first.");
            return;
        }

        if (parts.length < 2) {
            System.out.println("Invalid arguments.");
            return;
        }

        BookRecord record = findSingleBookRecord(parts[0], parts[1]).orElse(null);
        if (record == null) {
            return; // Message has been sent by findSingleBookRecord()
        }

        try {
            BorrowedBook borrowed = borrowStore.returnBook(currentLogin, record.getBook());
            System.out.println("Successfully returned book '" + record.getBook().getTitle() + "'!");
            if (borrowed.isOverdue()) {
                System.out.println("You have been fined $" + String.format("%.2f", borrowed.calculateOverdueFine()) + " due to returning the book after the due date.");
            }
        } catch (BorrowException e) {
            System.out.println(e.getMessage());
        }
    }
}
