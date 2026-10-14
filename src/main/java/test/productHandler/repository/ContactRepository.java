package test.productHandler.repository;

import org.springframework.stereotype.Repository;
import test.productHandler.entity.ContactBook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ContactRepository {

    private final Map<UUID, ContactBook> contactBooks = new HashMap<>();

    public List<ContactBook> findAll() {
        return new ArrayList<>(contactBooks.values());
    }

    public UUID save(ContactBook contactBook) {
        contactBooks.put(contactBook.getContactId(), contactBook);
        return contactBook.getContactId();
    }

    public Optional<ContactBook> findById(UUID taskId) {
        return Optional.ofNullable(contactBooks.get(taskId));
    }

    public void deleteById(UUID taskId) {
        contactBooks.remove(taskId);
    }

    public List<ContactBook> findByName(String firstName, String lastName) {
        return contactBooks.values().stream()
                .filter(contactBook -> firstName.equals(contactBook.getFirstName()) || lastName.equals(contactBook.getLastName()))
                .toList();
    }
}
