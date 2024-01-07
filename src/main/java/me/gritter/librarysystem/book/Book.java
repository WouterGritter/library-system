package me.gritter.librarysystem.book;

import me.gritter.librarysystem.Genre;

import java.util.Objects;

public class Book {

    private final String title;
    private final String author;
    private final String isbn;
    private final Genre genre;

    public Book(String title, String author, String isbn, Genre genre) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public Genre getGenre() {
        return genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(isbn, book.isbn) && genre == book.genre;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, isbn, genre);
    }

    @Override
    public String toString() {
        return '"' + title + "\"\n" +
                "Author: " + author + '\n' +
                "ISBN: " + isbn + '\n' +
                "Genre: " + genre;
    }
}
