package com.example.Ordering.menu;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuRequestDTO {
    @NotBlank(message = "Dish name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be at least 1")
    private Long price;

    @Min(value = 0, message = "Quantity cannot be negative")
    private long quantity = 100;

    private String imageUrl;
    private Long categoryId;
    private Boolean available = true;
    private Boolean veg = true;
    private Integer preparationTimeMinutes = 15;
}
