package com.example.Ordering.table;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurant_tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tableNumber;

    private Integer capacity = 4;
    private String section = "Indoor"; // Indoor, Outdoor Patio, Rooftop Garden, Window View
    private boolean active = true;
}
