package com.example.Ordering.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuUpdateRequestDto {
    private String name;
    private String description;
    private Long price;
    private Long quantity;
    private String imageUrl;
    private Long categoryId;
    private Boolean available;
    private Boolean veg;
    private Integer preparationTimeMinutes;
}
