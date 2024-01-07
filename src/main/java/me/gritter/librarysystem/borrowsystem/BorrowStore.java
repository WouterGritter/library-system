package me.gritter.librarysystem.borrowsystem;

import me.gritter.librarysystem.SimulatedDateTime;
import me.gritter.librarysystem.book.Book;
import me.gritter.librarysystem.book.BookRecord;
import me.gritter.librarysystem.book.BookStore;
import me.gritter.librarysystem.user.User;
import me.gritter.librarysystem.user.UserStore;

import java.time.Period;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableCollection;

public class BorrowStore {

    public static final int BOOK_BORROW_LIMIT = 2;
    public static final Period MAX_BOOK_BORROW_PERIOD = Period.ofDays(5);
    public static final double FINE_USD_PER_DAY = 1.50;

    private final UserStore userStore;
    private final BookStore bookStore;

    private final Collection<BorrowedBook> borrowedBooks = new HashSet<>();

    public BorrowStore(UserStore userStore, BookStore bookStore) {
        this.userStore = userStore;
        this.bookStore = bookStore;
    }

    public BorrowedBook borrowBook(User user, Book book) throws BorrowException {
        if (findBorrowedBooksByUser(user).count() >= BOOK_BORROW_LIMIT) {
            throw new BorrowException("You have borrowed the maximum amount of books. You have to return one first.");
        }

        BookRecord record = bookStore.getBookRecord(book);
        if (record.getQuantity() < 1) {
            throw new BorrowException("That book isn't available right now.");
        }

        if (findBorrowedBook(book, user).isPresent()) {
            throw new BorrowException("You have already borrowed that book.");
        }

        record.updateQuantity(-1);
        bookStore.cleanRecords();

        BorrowedBook borrowed = new BorrowedBook(book, user, SimulatedDateTime.now());
        borrowedBooks.add(borrowed);

        return borrowed;
    }

    public void returnBook(BorrowedBook borrowed) {
        if (!borrowedBooks.contains(borrowed)) {
            throw new IllegalArgumentException();
        }

        BookRecord record = bookStore.getBookRecord(borrowed.getBook());
        record.updateQuantity(1);

        borrowedBooks.remove(borrowed);
    }

    public Collection<BorrowedBook> getBorrowedBooks() {
        return unmodifiableCollection(borrowedBooks);
    }

    public Stream<BorrowedBook> findOverdueBooks() {
        return borrowedBooks.stream()
                .filter(BorrowedBook::isOverdue);
    }

    public Stream<BorrowedBook> findOverdueBooksByUser(User user) {
        return borrowedBooks.stream()
                .filter(bb -> bb.getUser().equals(user))
                .filter(BorrowedBook::isOverdue);
    }

    public Stream<BorrowedBook> findBorrowedBooksByUser(User user) {
        return borrowedBooks.stream()
                .filter(bb -> bb.getUser().equals(user));
    }

    public Stream<BorrowedBook> findBorrowedBooksByBook(Book book) {
        return borrowedBooks.stream()
                .filter(bb -> bb.getBook().equals(book));
    }

    public Optional<BorrowedBook> findBorrowedBook(Book book, User user) {
        return borrowedBooks.stream()
                .filter(bb -> bb.getBook().equals(book))
                .filter(bb -> bb.getUser().equals(user))
                .findAny();
    }

    public Optional<BorrowedBook> findBorrowedBook(String isbn, User user) {
        return borrowedBooks.stream()
                .filter(bb -> bb.getBook().getIsbn().equals(isbn))
                .filter(bb -> bb.getUser().equals(user))
                .findAny();
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public BookStore getBookStore() {
        return bookStore;
    }
}
