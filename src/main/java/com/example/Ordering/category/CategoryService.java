package com.example.Ordering.category;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository)
    {
        this.categoryRepository=categoryRepository;
    }


    public CategoryResponseDto createCategory(CategoryRequestDto category) {
        Category newCategory=mapToEntity(category);
        Category newCat=categoryRepository.save(newCategory);
        return mapToDto(newCat);
    }

    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories=categoryRepository.findAll();
        List<CategoryResponseDto> cats=new ArrayList<>();
        for(Category c:categories)
        {
            cats.add(mapToDto(c));
        }
        return cats;
    }

    public CategoryResponseDto getCategoryById(Long id) {
        Category cat=categoryRepository.findById(id).orElseThrow();
        CategoryResponseDto getCategory=mapToDto(cat);
        return getCategory;

    }

    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);

    }

    public CategoryUpdateResponseDto updateCategory(CategoryUpdateRequestDto categoryUpdateRequestDto, Long id) {
        Category c=categoryRepository.findById(id).orElseThrow();
        c.setName(categoryUpdateRequestDto.getName());
        c.setDescription(categoryUpdateRequestDto.getDescription());
        Category cat=categoryRepository.save(c);

        return  mapToUpdateDto(cat);
    }
    private CategoryResponseDto mapToDto(Category newCategory) {
        CategoryResponseDto catResponse=new CategoryResponseDto();
        catResponse.setId(newCategory.getId());
        catResponse.setName(newCategory.getName());
        catResponse.setDescription(newCategory.getDescription());
        return catResponse;
    }

    private Category mapToEntity(CategoryRequestDto categoryRequestDto)
    {
        Category newCat=new Category();
        newCat.setName(categoryRequestDto.getName());
        newCat.setDescription(categoryRequestDto.getDescription());

        return newCat;
    }
    private CategoryUpdateResponseDto mapToUpdateDto(Category c)
    {
        CategoryUpdateResponseDto cat=new CategoryUpdateResponseDto();
        cat.setId(c.getId());
        cat.setName(c.getName());
        cat.setDescription(c.getDescription());
        cat.setUpdatedAt(LocalDateTime.now());
        return cat;
    }
}
