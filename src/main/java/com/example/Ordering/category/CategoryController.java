package com.example.Ordering.category;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService)
    {
        this.categoryService=categoryService;
    }
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto category)
    {
        CategoryResponseDto response=categoryService.createCategory(category);
        return ResponseEntity.
                status(HttpStatus.CREATED).
                body(response);
    }
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories()
    {
        List<CategoryResponseDto> allCategories=categoryService.getAllCategories();
        return ResponseEntity.
                status(HttpStatus.OK).
                body(allCategories);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id)
    {
        CategoryResponseDto getCategory=categoryService.getCategoryById(id);
        return ResponseEntity.
                status(HttpStatus.FOUND).
                body(getCategory);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id)
    {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.
                status(HttpStatus.NO_CONTENT).
                body("Category Deleted");
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoryUpdateResponseDto> updateCategory(@RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto,
                                                                    @PathVariable Long id)
    {
        CategoryUpdateResponseDto updatedCategory=categoryService.updateCategory(categoryUpdateRequestDto,id);
        return ResponseEntity.
                status(HttpStatus.OK).
                body(updatedCategory);
    }
}
