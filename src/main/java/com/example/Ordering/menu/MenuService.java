package com.example.Ordering.menu;

import com.example.Ordering.category.Category;
import com.example.Ordering.category.CategoryRepository;
import com.example.Ordering.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    public MenuService(MenuRepository menuRepository, CategoryRepository categoryRepository) {
        this.menuRepository = menuRepository;
        this.categoryRepository = categoryRepository;
    }

    public MenuResponseDTO createMenu(MenuRequestDTO request) {
        Menu menu = new Menu();
        menu.setMenu_id(menuRepository.findMaxId() + 1);
        menu.setName(request.getName());
        menu.setDescription(request.getDescription());
        menu.setPrice(request.getPrice());
        menu.setQuantity(request.getQuantity());
        menu.setImageUrl(request.getImageUrl());
        menu.setAvailable(request.getAvailable() != null ? request.getAvailable() : true);
        menu.setVeg(request.getVeg() != null ? request.getVeg() : true);
        menu.setPreparationTimeMinutes(request.getPreparationTimeMinutes() != null ? request.getPreparationTimeMinutes() : 15);
        menu.setLast_update(LocalDateTime.now());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            menu.setCategory(category);
        }

        Menu saved = menuRepository.save(menu);
        return mapToDto(saved);
    }

    public List<MenuResponseDTO> getAllMenus() {
        return menuRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<MenuResponseDTO> getMenusByCategory(Long categoryId) {
        return menuRepository.findByCategory_Id(categoryId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<MenuResponseDTO> searchMenus(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllMenus();
        }
        return menuRepository.searchMenu(query.trim()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public MenuResponseDTO getMenuById(Integer id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        return mapToDto(menu);
    }

    public MenuResponseDTO updateMenu(Integer id, MenuUpdateRequestDto request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        if (request.getName() != null) menu.setName(request.getName());
        if (request.getDescription() != null) menu.setDescription(request.getDescription());
        if (request.getPrice() != null) menu.setPrice(request.getPrice());
        if (request.getQuantity() != null) menu.setQuantity(request.getQuantity());
        if (request.getImageUrl() != null) menu.setImageUrl(request.getImageUrl());
        if (request.getAvailable() != null) menu.setAvailable(request.getAvailable());
        if (request.getVeg() != null) menu.setVeg(request.getVeg());
        if (request.getPreparationTimeMinutes() != null) menu.setPreparationTimeMinutes(request.getPreparationTimeMinutes());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            menu.setCategory(category);
        }

        menu.setLast_update(LocalDateTime.now());
        Menu updated = menuRepository.save(menu);
        return mapToDto(updated);
    }

    public void deleteMenu(Integer id) {
        if (!menuRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu item not found with id: " + id);
        }
        menuRepository.deleteById(id);
    }

    public MenuResponseDTO mapToDto(Menu menu) {
        return MenuResponseDTO.builder()
                .menu_id(menu.getMenu_id())
                .name(menu.getName())
                .description(menu.getDescription())
                .price(menu.getPrice())
                .quantity(menu.getQuantity())
                .imageUrl(menu.getImageUrl())
                .category_id(menu.getCategory() != null ? menu.getCategory().getId() : null)
                .category_name(menu.getCategory() != null ? menu.getCategory().getName() : null)
                .available(menu.isAvailable())
                .veg(menu.isVeg())
                .preparationTimeMinutes(menu.getPreparationTimeMinutes())
                .last_update(menu.getLast_update())
                .build();
    }
}
