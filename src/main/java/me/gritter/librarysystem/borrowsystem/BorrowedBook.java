package me.gritter.librarysystem.borrowsystem;

import me.gritter.librarysystem.SimulatedDateTime;
import me.gritter.librarysystem.book.Book;
import me.gritter.librarysystem.user.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class BorrowedBook {

    private final Book book;
    private final User user;
    private final LocalDateTime borrowedSince;

    public BorrowedBook(Book book, User user, LocalDateTime borrowedSince) {
        this.book = book;
        this.user = user;
        this.borrowedSince = borrowedSince;
    }

    public boolean isOverdue() {
        return SimulatedDateTime.now().isAfter(getDueDate());
    }

    public double calculateOverdueFine() {
        if (!isOverdue()) {
            return 0.0;
        }

        long daysOverdue = Duration.between(getDueDate(), SimulatedDateTime.now()).toDays();
        daysOverdue = Math.max(daysOverdue, 1L);
        return daysOverdue * BorrowStore.FINE_USD_PER_DAY;
    }

    public Book getBook() {
        return book;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getBorrowedSince() {
        return borrowedSince;
    }

    public LocalDateTime getDueDate() {
        return borrowedSince.plus(BorrowStore.MAX_BOOK_BORROW_PERIOD);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowedBook that = (BorrowedBook) o;
        return Objects.equals(book, that.book) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book, user);
    }

    @Override
    public String toString() {
        return "BorrowedBook{" +
                "book=" + book.getTitle() +
                ", user=" + user.getName() +
                ", overdue=" + isOverdue() +
                ", overdueFine=" + calculateOverdueFine() +
                '}';
    }
}
