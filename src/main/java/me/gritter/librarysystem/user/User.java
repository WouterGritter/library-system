package me.gritter.librarysystem.user;

import java.util.Objects;
import java.util.UUID;

public class User {

    private final String name;
    private final String address;
    private final String contactInformation;
    private final UUID uuid;

    public User(String name, String address, String contactInformation, UUID uuid) {
        this.name = name;
        this.address = address;
        this.contactInformation = contactInformation;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(address, user.address) && Objects.equals(contactInformation, user.contactInformation) && Objects.equals(uuid, user.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, contactInformation, uuid);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", contactInformation='" + contactInformation + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}
