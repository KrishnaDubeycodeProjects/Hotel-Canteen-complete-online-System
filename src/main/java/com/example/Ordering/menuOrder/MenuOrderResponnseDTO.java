package com.example.Ordering.menuOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Kept the existing (misspelled) class name "MenuOrderResponnseDTO" so nothing else breaks. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuOrderResponnseDTO {
    private Long id;
    private Integer dish_quantity;
    private Integer price;
    private Long subTotal;
}
