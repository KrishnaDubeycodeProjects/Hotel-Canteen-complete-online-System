package com.example.Ordering.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateRequestDto {
    private String name;
    private String description;
//    private LocalDateTime updatedAt;
}
