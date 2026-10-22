package com.example.Ordering.menu;

import com.example.Ordering.category.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Menu {
    @Id
    private Integer menu_id;

    private String name;
    private String description;
    private Long price;
    private long quantity;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    private boolean available = true;
    private boolean veg = true;
    private Integer preparationTimeMinutes = 15;
    private LocalDateTime last_update = LocalDateTime.now();
}
