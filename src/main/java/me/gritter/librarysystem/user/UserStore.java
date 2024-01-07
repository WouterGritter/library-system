package me.gritter.librarysystem.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.unmodifiableCollection;

public class UserStore {

    private final Collection<User> users = new HashSet<>();

    public Collection<User> getUsers() {
        return unmodifiableCollection(users);
    }

    public User addUser(String name, String address, String contactInformation) {
        User user = new User(name, address, contactInformation, UUID.randomUUID());
        users.add(user);

        return user;
    }

    public Optional<User> findUser(UUID uuid) {
        return users.stream()
                .filter(user -> user.getUuid().equals(uuid))
                .findAny();
    }

    public void removeUser(User user) {
        users.remove(user);
    }
}
