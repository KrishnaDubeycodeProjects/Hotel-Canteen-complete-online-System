package com.example.Ordering.menuOrder;

import com.example.Ordering.menu.Menu;
import com.example.Ordering.order_details.Orders;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order_Items {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Orders order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private String dishName;
    private String dishImageUrl;

    @Column(nullable = false)
    private Integer dish_quantity;

    @Column(nullable = false)
    private Integer price;

    private Long subTotal;
}
