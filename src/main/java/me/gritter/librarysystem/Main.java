package me.gritter.librarysystem;

import me.gritter.librarysystem.book.Book;
import me.gritter.librarysystem.book.BookStore;
import me.gritter.librarysystem.borrowsystem.BorrowStore;
import me.gritter.librarysystem.cli.CliLibrarySystem;
import me.gritter.librarysystem.user.UserStore;

public class Main {

    public static void main(String[] args) {
        System.out.println("Maximum number of borrowed books per user: " + BorrowStore.BOOK_BORROW_LIMIT);
        System.out.println("Maximum period of borrowing a book: " + BorrowStore.MAX_BOOK_BORROW_PERIOD.getDays() + " days");
        System.out.println("Fine per day a book is overdue to be returned: $" + String.format("%.2f", BorrowStore.FINE_USD_PER_DAY));
        System.out.println();

        BookStore bookStore = new BookStore();
        bookStore.getBookRecord(new Book("Title", "Author", "ISBN", Genre.HUMOR)).setQuantity(3);
        bookStore.getBookRecord(new Book("Other title", "Other author", "OtherISBN", Genre.SCIENCE_FICTION)).setQuantity(1);

        UserStore userStore = new UserStore();
        userStore.addUser("User", "Awesome address", "info@example.com");

        BorrowStore borrowStore = new BorrowStore(userStore, bookStore);

        CliLibrarySystem library = new CliLibrarySystem(bookStore, userStore, borrowStore);
        library.runCommandLoop();
    }
}
