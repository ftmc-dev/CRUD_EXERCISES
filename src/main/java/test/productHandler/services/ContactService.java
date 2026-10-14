package test.productHandler.services;

import org.springframework.stereotype.Service;
import test.productHandler.entity.ContactBook;
import test.productHandler.exceptions.ContactNotFoundException;
import test.productHandler.repository.ContactRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository){
        this.contactRepository = contactRepository;
    }

    public UUID createNewContact(ContactBook contactBook) {
        contactRepository.save(contactBook);
        return contactBook.getContactId();
    }

    public void deleteContactById(UUID contactId) {
        contactRepository.findById(contactId)
                .orElseThrow(() -> new ContactNotFoundException("Contact with id " + contactId + " not found"));
        contactRepository.deleteById(contactId);
    }

    public List<ContactBook> getAllContact() {
        return contactRepository.findAll();
    }

    public ContactBook getContactById(UUID contactId) {
        return contactRepository.findById(contactId)
                .orElseThrow(() -> new ContactNotFoundException("Contact with id " + contactId + " not found"));
    }

    public void updateContact(ContactBook contactBook, UUID contactId) {
            ContactBook contactBookToUpdate = contactRepository.findById(contactId)
                    .orElseThrow(() -> new ContactNotFoundException("Contact with id " + contactId + " not found"));
            contactBookToUpdate.setEmail(contactBook.getEmail());
            contactBookToUpdate.setFirstName(contactBook.getFirstName());
            contactBookToUpdate.setLastName(contactBook.getLastName());
            contactBookToUpdate.setPhone(contactBook.getPhone());
            contactRepository.save(contactBookToUpdate);
        }

    public List<ContactBook> searchByName(String firstName, String lastName) {
        return contactRepository.findByName(firstName, lastName);
    }
}
