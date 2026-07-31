package com.example.Ordering.project_details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NOTE: Contact_Us entity currently only has an `id` field — no name/email/
 * message columns exist yet. Added the fields a contact form normally needs;
 * you'll need to add matching columns to the Contact_Us entity for this to persist.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactUsRequestDTO {
    private String name;
    private String email;
    private String subject;
    private String message;
}
