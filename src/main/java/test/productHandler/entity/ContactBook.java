package test.productHandler.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter

public class ContactBook {
    private UUID idGenerated = UUID.randomUUID();
    private final UUID contactId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

public ContactBook (String firstName, String lastName, String email, String phone) {
    this.contactId = idGenerated;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
}
}
