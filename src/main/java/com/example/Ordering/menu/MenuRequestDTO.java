package com.example.Ordering.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** last_update is stamped server-side, so it's excluded here. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuRequestDTO {
    private String description;
    private Long price;
    private long quantity;
}
