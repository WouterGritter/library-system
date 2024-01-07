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

    private void showAllBorrowedBooks() {
        System.out.println("All borrowed books:");
        borrowStore.getBorrowedBooks()
                .forEach(System.out::println);
    }

    private void showOverdueBooks() {
        System.out.println("All overdue books:");
        borrowStore.findOverdueBooks()
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

        if (parts.length < 1) {
            System.out.println("Invalid arguments.");
            return;
        }

        BookRecord record = bookStore.findBookRecordByIsbn(parts[0]).findAny().orElse(null);
        if (record == null) {
            System.out.println("Could not find a book with ISBN " + parts[0] + ".");
            return;
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

        if (parts.length < 1) {
            System.out.println("Invalid arguments.");
            return;
        }

        BorrowedBook borrowed = borrowStore.findBorrowedBook(parts[0], currentLogin).orElse(null);
        if (borrowed == null) {
            System.out.println("Could not find a borrowed book with ISBN " + parts[0] + ".");
            return;
        }

        borrowStore.returnBook(borrowed);
        System.out.println("Successfully returned book '" + borrowed.getBook().getTitle() + "'!");
        if (borrowed.isOverdue()) {
            System.out.println("You have been fined $" + String.format("%.2f", borrowed.calculateOverdueFine()) + " due to returning the book after the due date.");
        }
    }
}
