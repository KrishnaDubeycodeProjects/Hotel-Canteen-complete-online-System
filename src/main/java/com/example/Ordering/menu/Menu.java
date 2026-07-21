package com.example.Ordering.menu;

import com.example.Ordering.entity.Dish_Category;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    private String description;
    private Long price;
    private long quantity;
    private LocalDateTime last_update;


}

