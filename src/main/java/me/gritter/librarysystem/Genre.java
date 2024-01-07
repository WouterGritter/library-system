package me.gritter.librarysystem;

import java.util.Optional;

public enum Genre {
    FICTION,
    NON_FICTION,
    MYSTERY,
    SCIENCE_FICTION,
    FANTASY,
    ROMANCE,
    HORROR,
    THRILLER,
    BIOGRAPHY,
    HISTORY,
    SELF_HELP,
    BUSINESS,
    CHILDREN,
    POETRY,
    SCIENCE,
    TRAVEL,
    COOKING,
    ART,
    PHILOSOPHY,
    RELIGION,
    MUSIC,
    SPORTS,
    HUMOR,
    EDUCATION;

    public static Optional<Genre> parseGenre(String name) {
        for (Genre genre : values()) {
            if (genre.name().equalsIgnoreCase(name)) {
                return Optional.of(genre);
            }
        }

        return Optional.empty();
    }
}
