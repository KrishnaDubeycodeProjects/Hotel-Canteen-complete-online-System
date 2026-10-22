package com.example.Ordering.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuResponseDTO {
    private Integer menu_id;
    private String name;
    private String description;
    private Long price;
    private long quantity;
    private String imageUrl;
    private Long category_id;
    private String category_name;
    private boolean available;
    private boolean veg;
    private Integer preparationTimeMinutes;
    private LocalDateTime last_update;
}