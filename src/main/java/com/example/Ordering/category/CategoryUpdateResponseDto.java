package com.example.Ordering.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime updatedAt;
}
