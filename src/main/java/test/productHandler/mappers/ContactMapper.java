package test.productHandler.mappers;

import org.mapstruct.Mapper;
import test.productHandler.dto.ContactCreateDTO;
import test.productHandler.dto.ContactDTO;
import test.productHandler.dto.UpdateContactRequestDTO;
import test.productHandler.entity.ContactBook;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    default ContactBook fromContactCreateDTOToContactBook(ContactCreateDTO contactCreateDTO) {
        return new ContactBook(contactCreateDTO.getFirstName(), contactCreateDTO.getLastName(), contactCreateDTO.getEmail(), contactCreateDTO.getPhone());
    }

    default ContactDTO fromContactBookToContactDTO(ContactBook contactBook) {
        return new ContactDTO(contactBook.getContactId(), contactBook.getFirstName(), contactBook.getLastName(), contactBook.getEmail());
    }

    default ContactBook fromUpdateRequestDTOToContactBook(UpdateContactRequestDTO updateContactRequestDTO) {
        return new ContactBook(updateContactRequestDTO.getFirstName(), updateContactRequestDTO.getLastName(), updateContactRequestDTO.getEmail(), updateContactRequestDTO.getPhone());
    }

}
