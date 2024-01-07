package me.gritter.librarysystem.cli;

import me.gritter.librarysystem.Utils;
import me.gritter.librarysystem.user.User;
import me.gritter.librarysystem.user.UserStore;

import java.util.UUID;

import static java.util.Arrays.copyOfRange;

public class CliUserStoreSystem implements CliSystem {

    private final UserStore userStore;

    public CliUserStoreSystem(UserStore userStore) {
        this.userStore = userStore;
    }

    @Override
    public boolean parseCommand(String[] parts) {
        if (parts.length < 1) {
            return false;
        }

        String[] commandParts = copyOfRange(parts, 1, parts.length);

        switch (parts[0]) {
            case "l":
                listUsersCommand();
                return true;
            case "a":
                addUserCommand(commandParts);
                return true;
            case "d":
                deleteUserCommand(commandParts);
                return true;
            default:
                return false;
        }
    }

    private void listUsersCommand() {
        System.out.println("Registered users:");
        for (User user : userStore.getUsers()) {
            System.out.println(" " + user.toString());
        }
    }

    private void addUserCommand(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Invalid arguments.");
            return;
        }

        String name = parts[0];
        String address = parts[1];
        String contactInformation = parts[2];

        User user = userStore.addUser(name, address, contactInformation);
        System.out.println("A new user has been created: " + user);
    }

    private void deleteUserCommand(String[] parts) {
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

        userStore.removeUser(user);
        System.out.println("The following user has been deleted: " + user);
    }
}
