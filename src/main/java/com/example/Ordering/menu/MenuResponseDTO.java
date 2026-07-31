package com.example.Ordering.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponseDTO {
    private Integer menu_id;
    private String description;
    private Long price;
    private long quantity;
    private LocalDateTime last_update;

    // --- requires Menu entity to actually reference these ---
    private Long category_id;
    private String category_name;   // from Menu_Category
    private Byte[] image;           // from Menu_image
}