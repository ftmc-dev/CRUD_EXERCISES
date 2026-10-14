package test.productHandler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import test.productHandler.api.ContactApi;
import test.productHandler.dto.ContactCreateDTO;
import test.productHandler.dto.ContactDTO;
import test.productHandler.dto.ContactResponseDTO;
import test.productHandler.dto.UpdateContactRequestDTO;
import test.productHandler.entity.ContactBook;
import test.productHandler.mappers.ContactMapper;
import test.productHandler.services.ContactService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ContactController implements ContactApi {
    private final ContactService contactService;
    private final ContactMapper contactMapper;

    @Override
    public ResponseEntity<ContactResponseDTO> createContacts(ContactCreateDTO contactCreateDTO) {
        UUID uuid = contactService.createNewContact(contactMapper.fromContactCreateDTOToContactBook(contactCreateDTO));
        return ResponseEntity.status(201).body(new ContactResponseDTO(uuid));
    }

    @Override
    public ResponseEntity<Void> deleteContactById(UUID contactId) {
        contactService.deleteContactById(contactId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ContactDTO>> getAllContacts() {
        List<ContactBook> result = contactService.getAllContact();
        List<ContactDTO> contactDTO = result.stream()
                .map(contactMapper::fromContactBookToContactDTO)
                .toList();

        return ResponseEntity.status(200).body(contactDTO);
    }

    @Override
    public ResponseEntity<ContactDTO> getContactById(UUID contactId) {
        ContactBook contactBook = contactService.getContactById(contactId);
        return ResponseEntity.status(200).body(null);
    }

    @Override
    public ResponseEntity<List<ContactDTO>> searchContactByName(String firstName, String lastName) {
        List<ContactBook> contactBooks = contactService.searchByName(firstName, lastName);
        List<ContactDTO> contactDTOS = contactBooks.stream()
                .map(contactMapper::fromContactBookToContactDTO)
                .toList();
        return ResponseEntity.status(200).body(contactDTOS);
    }

    @Override
    public ResponseEntity<Void> updateContact(UUID contactId, UpdateContactRequestDTO updateContactRequestDTO) {
       ContactBook contactBookToUpdate = contactMapper.fromUpdateRequestDTOToContactBook(updateContactRequestDTO);
       contactService.updateContact(contactBookToUpdate, contactId);
       return ResponseEntity.noContent().build();
    }
}
