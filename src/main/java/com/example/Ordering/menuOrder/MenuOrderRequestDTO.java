package com.example.Ordering.menuOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuOrderRequestDTO {
    private Integer menu_id;
    private Long order_id;
    private Integer dish_quantity;
}
