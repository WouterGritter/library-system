package me.gritter.librarysystem.cli;

import me.gritter.librarysystem.book.BookStore;
import me.gritter.librarysystem.borrowsystem.BorrowStore;
import me.gritter.librarysystem.user.UserStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.util.Arrays.copyOfRange;

public class CliLibrarySystem implements CliSystem {

    private final CliTimeSystem cliTime;
    private final CliBookStoreSystem cliBookStore;
    private final CliUserStoreSystem cliUserStore;
    private final CliBorrowStoreSystem cliBorrowStore;

    public CliLibrarySystem(BookStore bookStore, UserStore userStore, BorrowStore borrowStore) {
        cliTime = new CliTimeSystem();
        cliBookStore = new CliBookStoreSystem(bookStore);
        cliUserStore = new CliUserStoreSystem(userStore);
        cliBorrowStore = new CliBorrowStoreSystem(borrowStore);
    }

    public void runCommandLoop() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to the library system.");
        helpCommand();

        while (true) {
            System.out.println();
            System.out.print("Command (h for help): ");
            String command = sc.nextLine();

            String[] parts = splitString(command);

            boolean ok = parseCommand(parts);
            if (!ok) {
                System.out.println("Invalid command. Type 'h' for help.");
            }
        }
    }

    private String[] splitString(String str) {
        List<String> result = new ArrayList<>();

        StringBuilder currentPart = new StringBuilder();
        boolean inQuotedPart = false;
        for (char c : str.toCharArray()) {
            if (c == '"') {
                if (!inQuotedPart) {
                    inQuotedPart = true;
                } else {
                    inQuotedPart = false;
                    String trimmed = currentPart.toString().trim();
                    if (!trimmed.isEmpty()) {
                        result.add(trimmed);
                    }
                    currentPart = new StringBuilder();
                }
            } else if (c == ' ' && !inQuotedPart) {
                String trimmed = currentPart.toString().trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
                currentPart = new StringBuilder();
            } else {
                currentPart.append(c);
            }
        }

        String trimmed = currentPart.toString().trim();
        if (!trimmed.isEmpty()) {
            result.add(trimmed);
        }

        return result.toArray(new String[0]);
    }

    @Override
    public boolean parseCommand(String[] parts) {
        if (parts.length < 1) {
            return false;
        }

        String[] commandParts = copyOfRange(parts, 1, parts.length);

        switch (parts[0]) {
            case "h": // Help
                helpCommand();
                return true;
            case "t": // Time
                return cliTime.parseCommand(commandParts);
            case "b": // Books
                return cliBookStore.parseCommand(commandParts);
            case "u": // Users
                return cliUserStore.parseCommand(commandParts);
            case "B": // Borrow
                return cliBorrowStore.parseCommand(commandParts);
            default:
                return false;
        }
    }

    private void helpCommand() {
        System.out.println("The commands and their arguments are case sensitive.");
        System.out.println("Arguments with spaces can be formatted by enclosing them in double-quotes (\").");
        System.out.println("Commands:");
        System.out.println(" h - Show this help");
        System.out.println();
        System.out.println("Time commands:");
        System.out.println(" t t - Show the current simulated date/time");
        System.out.println(" t a - Advance the simulated date/time by 1 day");
        System.out.println();
        System.out.println("General/user commands:");
        System.out.println(" b l - List all books and their quantities");
        System.out.println(" b s <'title'|'author'|'isbn'|'genre'> <value> - Search a book and show its quantity");
        System.out.println(" B l <uuid> - Login as a specific user");
        System.out.println(" B s - Show your borrowed books");
        System.out.println(" B b <'title'|'author'|'isbn'|'genre'> <value> - Borrow a book");
        System.out.println(" B r <'title'|'author'|'isbn'|'genre'> <value> - Return a book");
        System.out.println();
        System.out.println("Management commands:");
        System.out.println(" b a <title> <author> <isbn> <genre> [quantity] - Add one or more books");
        System.out.println(" b q <'title'|'author'|'isbn'|'genre'> <value> <quantity delta> - Update the quantity of a book");
        System.out.println(" b d <'title'|'author'|'isbn'|'genre'> <value> - Remove all copies of a book");
        System.out.println(" b g - List all genres");
        System.out.println(" u l - List all users");
        System.out.println(" u a <name> <address> <contact information> - Add a new user");
        System.out.println(" u d <uuid> - Delete an existing user");
        System.out.println(" B S - Show all borrowed books");
        System.out.println(" B O - Show all overdue books and their fines");
        System.out.println();
        System.out.println("User commands:");
    }
}
