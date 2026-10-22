package com.example.Ordering.menu;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/v1/api/menu", "/api/menu"})
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping
    public ResponseEntity<MenuResponseDTO> createMenu(@Valid @RequestBody MenuRequestDTO request) {
        MenuResponseDTO created = menuService.createMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<MenuResponseDTO>> getAllMenus() {
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuResponseDTO> getMenuById(@PathVariable Integer id) {
        return ResponseEntity.ok(menuService.getMenuById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MenuResponseDTO>> getMenusByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(menuService.getMenusByCategory(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MenuResponseDTO>> searchMenus(@RequestParam("query") String query) {
        return ResponseEntity.ok(menuService.searchMenus(query));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuResponseDTO> updateMenu(@PathVariable Integer id,
                                                      @RequestBody MenuUpdateRequestDto request) {
        return ResponseEntity.ok(menuService.updateMenu(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenu(@PathVariable Integer id) {
        menuService.deleteMenu(id);
        return ResponseEntity.ok("Menu item deleted successfully");
    }
}
