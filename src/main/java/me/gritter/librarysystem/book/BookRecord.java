package me.gritter.librarysystem.book;

public class BookRecord {

    private final Book book;

    private int quantity;

    public BookRecord(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public BookRecord(Book book) {
        this(book, 0);
    }

    public Book getBook() {
        return book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void updateQuantity(int delta) {
        this.quantity += delta;
    }

    @Override
    public String toString() {
        return book.toString() + '\n' +
                "Quantity: " + quantity;
    }
}
