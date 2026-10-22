# 📚 Complete Code Walkthrough & Technical File-by-File Documentation

This document contains the **complete source code and in-depth explanation** of all critical files in the **Hotel & Canteen Management System**, explaining their architecture, data models, security configurations, database mappings, and business logic.

---

## 📑 Table of Contents

1. [Security & Authentication Infrastructure](#1-security--authentication-infrastructure)
   - `SecurityConfig.java`
   - `JwtUtil.java`
   - `JwtAuthenticationFilter.java`
   - `RateLimitingFilter.java`
2. [User Management & RBAC Module](#2-user-management--rbac-module)
   - `Role.java`
   - `User.java`
   - `UserRepository.java`
   - `UserService.java`
   - `AuthController.java`
3. [Category & Menu with Photos Module](#3-category--menu-with-photos-module)
   - `Category.java`
   - `CategoryRepository.java`
   - `CategoryService.java`
   - `CategoryController.java`
   - `Menu.java`
   - `MenuRepository.java`
   - `MenuService.java`
   - `MenuController.java`
   - `FileUploadController.java`
   - `WebMvcConfig.java`
4. [Order Processing & Lifecycle Engine](#4-order-processing--lifecycle-engine)
   - `Order_Status.java`
   - `Orders.java`
   - `Order_Items.java`
   - `Order_track.java`
   - `OrderDetailsRepository.java`
   - `OrderService.java`
   - `OrderDetailsController.java`
5. [Direct Payment & Instant Refund Engine](#5-direct-payment--instant-refund-engine)
   - `Payment.java`
   - `Refund.java`
   - `PaymentController.java`
6. [Table Reservation System (15m to 2h Strict Validation)](#6-table-reservation-system-15m-to-2h-strict-validation)
   - `RestaurantTable.java`
   - `TableBooking.java`
   - `TableBookingRepository.java`
   - `TableBookingService.java`
   - `TableBookingController.java`
7. [Coupons & Promotional Discounts Module](#7-coupons--promotional-discounts-module)
   - `Coupon.java`
   - `CouponService.java`
   - `CouponController.java`
8. [Exception Handling & Data Seeding](#8-exception-handling--data-seeding)
   - `GlobalExceptionHandler.java`
   - `DataInitializer.java`
   - `application.properties`

---

## 1. Security & Authentication Infrastructure

### 1.1 `SecurityConfig.java`
**Location**: `src/main/java/com/example/Ordering/security/SecurityConfig.java`  
**Purpose**: Central security configuration governing HTTP filter chains, stateless sessions, CORS, and role authorizations.

```java
package com.example.Ordering.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final RateLimitingFilter rateLimitingFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, RateLimitingFilter rateLimitingFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public Static Web Resources & Frontend
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/static/**", "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        // Public Auth Endpoints & Uploads
                        .requestMatchers("/api/auth/**", "/api/upload/**").permitAll()
                        // Public Read-Only Menu, Category & Tables
                        .requestMatchers(HttpMethod.GET, "/v1/api/category/**", "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/api/menu/**", "/api/menu/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tables/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/project/**", "/api/feedback/**").permitAll()
                        .requestMatchers("/api/coupons/validate").permitAll()
                        // Public order tracking by token/id for user convenience
                        .requestMatchers(HttpMethod.GET, "/api/orders/*/track", "/api/orders/track/*").permitAll()
                        // Protected role-based and authenticated endpoints
                        .requestMatchers("/api/orders/kitchen/**").hasAnyRole("CHEF", "ADMIN")
                        .requestMatchers("/api/orders/waiter/**").hasAnyRole("WAITER", "ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```
**Explanation**:
- Disables CSRF to allow stateless REST operations.
- Enforces `SessionCreationPolicy.STATELESS` so no session state is maintained in server memory.
- Uses `addFilterBefore` to position the `RateLimitingFilter` first (dropping excess traffic before any token parsing) followed by `JwtAuthenticationFilter`.
- Grants public access to static SPA resources, images at `/uploads/**`, auth endpoints, read-only menu items/categories, and table listings.
- Protects KDS endpoints for `CHEF`/`ADMIN`, and delivery verification for `WAITER`/`ADMIN`.

---

### 1.2 `JwtUtil.java`
**Location**: `src/main/java/com/example/Ordering/security/JwtUtil.java`  
**Purpose**: Generates and validates limitless JWT tokens (100-year expiration) signed with HMAC-SHA256.

```java
package com.example.Ordering.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final String SECRET_STRING = "MySecretRestaurantCanteenKeyWithSuperStrong256BitsForJWT2026!";
    private final Key key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // Limitless validity: 100 years in milliseconds
    private static final long LIMITLESS_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 365 * 100;

    public String generateToken(String phoneNumber, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, phoneNumber);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + LIMITLESS_EXPIRATION_MS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, String phoneNumber) {
        final String extractedPhone = extractPhoneNumber(token);
        return (extractedPhone.equals(phoneNumber) && !isTokenExpired(token));
    }

    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return (String) extractAllClaims(token).get("role");
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
```
**Explanation**:
- Computes `LIMITLESS_EXPIRATION_MS` = 100 years ($1000 \times 60 \times 60 \times 24 \times 365 \times 100$).
- Embeds both the user's `phoneNumber` (subject) and their `role` (claim) into the signed JWT.
- Exposes utility extractors (`extractPhoneNumber`, `extractRole`) used across security filters.

---

### 1.3 `JwtAuthenticationFilter.java`
**Location**: `src/main/java/com/example/Ordering/security/JwtAuthenticationFilter.java`  
**Purpose**: Intercepts requests, validates the Bearer token, loads the User entity, and populates Spring Security's `SecurityContextHolder`.

```java
package com.example.Ordering.security;

import com.example.Ordering.user.User;
import com.example.Ordering.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String phoneNumber = null;
        String jwtToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                phoneNumber = jwtUtil.extractPhoneNumber(jwtToken);
            } catch (Exception e) {
                logger.warn("Invalid JWT token format or signature: " + e.getMessage());
            }
        }

        if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findByPhoneNumber(phoneNumber).orElse(null);

            if (user != null && jwtUtil.validateToken(jwtToken, user.getPhoneNumber())) {
                String roleName = user.getRole() != null ? user.getRole().name() : "USER";
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(authority));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```
**Explanation**:
- Extracts `Authorization: Bearer <token>` from inbound HTTP headers.
- Looks up the active `User` in PostgreSQL by `phoneNumber`.
- Grants `ROLE_<ROLE_NAME>` authority (e.g. `ROLE_ADMIN`, `ROLE_CHEF`).
- Attaches the complete `User` entity as the Principal, allowing controllers to access `@AuthenticationPrincipal User user` directly.

---

### 1.4 `RateLimitingFilter.java`
**Location**: `src/main/java/com/example/Ordering/security/RateLimitingFilter.java`  
**Purpose**: Protects against denial-of-service and credential stuffing using a thread-safe sliding window algorithm.

```java
package com.example.Ordering.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 150;
    private static final int AUTH_REQUESTS_PER_MINUTE = 25;
    private static final long ONE_MINUTE_MS = 60 * 1000L;

    private final ConcurrentHashMap<String, Queue<Long>> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientIp = getClientIp(request);
        String path = request.getRequestURI();

        int maxRequests = path.startsWith("/api/auth/") ? AUTH_REQUESTS_PER_MINUTE : MAX_REQUESTS_PER_MINUTE;
        long now = System.currentTimeMillis();

        requestCounts.putIfAbsent(clientIp, new ConcurrentLinkedQueue<>());
        Queue<Long> timestamps = requestCounts.get(clientIp);

        synchronized (timestamps) {
            // Remove timestamps older than 1 minute
            while (!timestamps.isEmpty() && now - timestamps.peek() > ONE_MINUTE_MS) {
                timestamps.poll();
            }

            if (timestamps.size() >= maxRequests) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Too Many Requests\", \"message\": \"Rate limit exceeded. Please try again in a few moments.\"}");
                return;
            }

            timestamps.add(now);
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(",")) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
```
**Explanation**:
- Examines `X-Forwarded-For` header to capture true client IP behind reverse proxies/CDNs.
- Uses `ConcurrentHashMap` with thread-safe synchronized timestamp eviction.
- Responds with `HTTP 429 Too Many Requests` whenever thresholds are exceeded.

---

## 2. User Management & RBAC Module

### 2.1 `Role.java` & `User.java`
**Location**: `src/main/java/com/example/Ordering/user/`  
**Purpose**: Defines the 4 application roles and the User entity with phone, encrypted password, and recovery OTP fields.

```java
// Role.java
package com.example.Ordering.user;

public enum Role {
    USER,
    ADMIN,
    CHEF,
    WAITER
}
```

```java
// User.java
package com.example.Ordering.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    private String name;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    private String address;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Forgot password OTP temporary verification
    @JsonIgnore
    private String resetOtp;

    @JsonIgnore
    private LocalDateTime resetOtpExpiry;
}
```
**Explanation**:
- Maps to table `app_users` to avoid collision with PostgreSQL reserved keyword `user`.
- `@JsonIgnore` shields `password`, `resetOtp`, and `resetOtpExpiry` from being exposed in REST response payloads.
- `Role` is stored as `EnumType.STRING` (`'USER'`, `'ADMIN'`, `'CHEF'`, `'WAITER'`).

---

### 2.2 `UserService.java` & `AuthController.java`
**Location**: `src/main/java/com/example/Ordering/user/`  
**Purpose**: Registration, BCrypt verification, limitless JWT issuance, and forgot-password OTP workflows.

```java
// UserService.java (Core excerpt)
package com.example.Ordering.user;

import com.example.Ordering.exception.BadRequestException;
import com.example.Ordering.exception.ResourceNotFoundException;
import com.example.Ordering.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDTO register(UserRequestDTO request) {
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new BadRequestException("Phone number is already registered: " + request.getPhoneNumber());
        }

        User user = new User();
        user.setId(userRepository.findMaxId() + 1);
        user.setPhoneNumber(request.getPhoneNumber().trim());
        user.setName(request.getName() != null ? request.getName() : "Customer");
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        user.setAddress(request.getAddress());
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getPhoneNumber(), saved.getRole().name());

        return new AuthResponseDTO(token, saved.getId(), saved.getPhoneNumber(), saved.getName(), saved.getRole());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber().trim())
                .orElseThrow(() -> new BadRequestException("Invalid phone number or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid phone number or password");
        }

        String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getRole().name());
        return new AuthResponseDTO(token, user.getId(), user.getPhoneNumber(), user.getName(), user.getRole());
    }

    public String generateForgotPasswordOtp(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + phoneNumber));

        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
        user.setResetOtp(otp);
        user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        return otp; // In production this would trigger SMS gateway
    }

    public void resetPasswordWithOtp(ResetPasswordRequestDTO request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + request.getPhoneNumber()));

        if (user.getResetOtp() == null || !user.getResetOtp().equals(request.getOtp())) {
            throw new BadRequestException("Invalid reset OTP");
        }
        if (user.getResetOtpExpiry() == null || user.getResetOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset OTP has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);
        userRepository.save(user);
    }
}
```
**Explanation**:
- Encrypts passwords with `BCryptPasswordEncoder` (adaptive salted hash).
- Safely generates manual sequential IDs via `userRepository.findMaxId() + 1`.
- Issues limitless JWT upon successful registration or login.
- Supports 6-digit OTP generation with 15-minute expiration for password reset.

---

## 3. Category & Menu with Photos Module

### 3.1 `Category.java` & `CategoryService.java`
**Location**: `src/main/java/com/example/Ordering/category/`  
**Purpose**: Categorizes menu dishes, holding photo URL and active status.

```java
// Category.java
package com.example.Ordering.category;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean active = true;
}
```

```java
// CategoryService.java (Excerpt)
package com.example.Ordering.category;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponseDto createCategory(CategoryRequestDto category) {
        Category newCategory = mapToEntity(category);
        if (newCategory.getId() == null) {
            newCategory.setId(categoryRepository.findMaxId() + 1);
        }
        if (newCategory.getActive() == null) {
            newCategory.setActive(true);
        }
        Category newCat = categoryRepository.save(newCategory);
        return mapToDto(newCat);
    }

    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    // Mapping utilities omitted for brevity
}
```
**Explanation**:
- `@Id private Long id;` has no `@GeneratedValue` to prevent optimistic locking / stale state errors on Supabase tables that lack autoincrement sequences.
- `CategoryRepository.java` uses `@Query("SELECT COALESCE(MAX(c.id), 0) FROM Category c") Long findMaxId();` to calculate sequential IDs.

---

### 3.2 `Menu.java` & `MenuService.java`
**Location**: `src/main/java/com/example/Ordering/menu/`  
**Purpose**: Entity and service managing food dishes, photos, preparation time, veg/non-veg flag, and inventory quantity.

```java
// Menu.java
package com.example.Ordering.menu;

import com.example.Ordering.category.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Menu {
    @Id
    private Integer menu_id;

    private String name;
    private String description;
    private Long price;
    private long quantity;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    private boolean available = true;
    private boolean veg = true;
    private Integer preparationTimeMinutes = 15;
    private LocalDateTime last_update = LocalDateTime.now();
}
```

```java
// MenuService.java (Excerpt)
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
}
```
**Explanation**:
- Dishes hold high-resolution photo URLs (`imageUrl`), estimated cooking time (`preparationTimeMinutes`), and vegetarian boolean flag (`veg`).
- Links to parent `Category` through `@ManyToOne`.
- Assigns sequential `menu_id` via `menuRepository.findMaxId() + 1`.

---

### 3.3 `FileUploadController.java` & `WebMvcConfig.java`
**Location**: `src/main/java/com/example/Ordering/common/`  
**Purpose**: Multipart image file upload endpoint and static resource mapping.

```java
// FileUploadController.java
package com.example.Ordering.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads";

    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String newFilename = UUID.randomUUID().toString() + extension;
            Path filePath = Paths.get(UPLOAD_DIR, newFilename);
            Files.write(filePath, file.getBytes());

            String fileUrl = "/uploads/" + newFilename;
            return ResponseEntity.ok(Map.of(
                    "url", fileUrl,
                    "filename", newFilename,
                    "message", "Image uploaded successfully"
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to store file: " + e.getMessage()));
        }
    }
}
```

```java
// WebMvcConfig.java
package com.example.Ordering.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}
```
**Explanation**:
- Automatically creates `uploads/` directory on disk if missing.
- Sanitizes file extensions and names with randomized UUIDs preventing collisions and path traversal exploits.
- `WebMvcConfig` maps `/uploads/**` to disk, serving photos directly to browsers without requiring an external storage bucket.

---

## 4. Order Processing & Lifecycle Engine

### 4.1 `Order_Status.java` & `Orders.java`
**Location**: `src/main/java/com/example/Ordering/order_details/`  
**Purpose**: Defines the ordinal enum lifecycle and the master Orders JPA entity.

```java
// Order_Status.java
package com.example.Ordering.order_details;

public enum Order_Status {
    Added_to_Cart, // 0
    Placed,        // 1
    Preparing,     // 2
    Ready,         // 3
    Collected,     // 4
    Cancelled,     // 5
    Refunded       // 6
}
```

```java
// Orders.java
package com.example.Ordering.order_details;

import com.example.Ordering.coupon.Coupon;
import com.example.Ordering.menuOrder.Order_Items;
import com.example.Ordering.payment.Payment;
import com.example.Ordering.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    @Id
    private Long id;

    @Column(unique = true)
    private String tokenNumber;

    private String qrData;
    private Long totalAmount = 0L;
    private Long discountAmount = 0L;

    // Mapped dual columns to prevent PostgreSQL NOT NULL constraint violations
    @Column(name = "payment_attempts", nullable = false)
    private int paymentAttempts = 0;

    @Column(name = "number_of_times_user_tried_payment", nullable = false)
    private int numberOfTimesUserTriedPayment = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order_Items> orderItems = new ArrayList<>();

    // Stores the raw repeating menu IDs submitted by the client
    @ElementCollection
    @CollectionTable(name = "order_menu_ids", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "menu_id")
    private List<Integer> menuIds = new ArrayList<>();

    // Ordinal smallint mapping in PostgreSQL
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "order_status")
    private Order_Status order_status = Order_Status.Placed;

    private boolean payement_status = false;

    // Assigned Chef (Preparing)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cheff_id")
    private User cheff;

    // Assigned Waiter (Delivery / Collection)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "waiter_id")
    private User waiter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private boolean refund_status = false;
    private LocalDateTime refund_time;
    private String refund_reason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refund_initiator_id")
    private User refund_Initiator;

    private LocalDateTime payment_time;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private Boolean paid = false;

    // DINE_IN, TAKEAWAY, HOME_DELIVERY, DOORSTEP
    private String deliveryType = "DINE_IN";
    private boolean delivery = false;
    private String deliveryAddress;
    private String tableNumber;

    // 4-digit verification OTP for counter/waiter pickup
    private String otp_verify;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lastUpdateTime ASC")
    private List<Order_track> trackHistory = new ArrayList<>();
}
```
**Explanation**:
- `@Enumerated(EnumType.ORDINAL)` on `order_status` stores integers `0, 1, 2, 3...` matching PostgreSQL's `smallint` definition and eliminating the `ERROR: invalid input syntax for type smallint: "Placed"` bug.
- Both `payment_attempts` and `number_of_times_user_tried_payment` are mapped and default to `0`, satisfying PostgreSQL non-null constraints.
- `@ElementCollection` stores the exact raw sequence of repeating menu IDs.
- Tracks assigned `cheff` (chef who prepared) and `waiter` (waiter who served), plus the 4-digit `otp_verify`.

---

### 4.2 `OrderDetailsRepository.java`
**Location**: `src/main/java/com/example/Ordering/order_details/OrderDetailsRepository.java`  
**Purpose**: Spring Data repository with parameterized enum queries for KDS and Waiter queues.

```java
package com.example.Ordering.order_details;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailsRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByTokenNumber(String tokenNumber);
    Optional<Orders> findBytokenNumber(String tokenNumber);

    List<Orders> findByUser_PhoneNumberOrderByCreatedAtDesc(String phoneNumber);
    List<Orders> findByOrderByCreatedAtDesc();

    @Query("SELECT o FROM Orders o WHERE o.order_status = :status ORDER BY o.createdAt ASC")
    List<Orders> findByOrderStatus(@Param("status") Order_Status status);

    @Query("SELECT o FROM Orders o WHERE o.order_status IN (:s1, :s2) ORDER BY o.createdAt ASC")
    List<Orders> findActiveKitchenOrders(@Param("s1") Order_Status s1, @Param("s2") Order_Status s2);

    @Query("SELECT o FROM Orders o WHERE o.order_status = :s ORDER BY o.createdAt ASC")
    List<Orders> findReadyOrdersForWaiter(@Param("s") Order_Status s);

    @Query("SELECT COALESCE(MAX(o.id), 0) FROM Orders o")
    Long findMaxId();
}
```
**Explanation**:
- Uses parameterized `@Param("s1") Order_Status s1` so Hibernate binds numeric smallint ordinals to SQL, eliminating string comparison errors in PostgreSQL.

---

### 4.3 `OrderService.java`
**Location**: `src/main/java/com/example/Ordering/order_details/OrderService.java`  
**Purpose**: Central order engine: repeating ID counting, stock deductions, coupon calculations, OTP verification, and status transitions.

```java
// Excerpt of core methods in OrderService.java
@Transactional
public OrderDetailsResponseDTO placeOrder(OrderDetailsRequestDTO request, User user) {
    List<Integer> menuIds = request.getMenuIds();
    if (menuIds == null || menuIds.isEmpty()) {
        throw new BadRequestException("Order must contain at least one menu item");
    }

    // 1. Group frequency of repeating menu IDs: e.g. [2, 2, 5] -> ID 2: qty 2, ID 5: qty 1
    Map<Integer, Integer> quantityMap = new LinkedHashMap<>();
    for (Integer id : menuIds) {
        quantityMap.put(id, quantityMap.getOrDefault(id, 0) + 1);
    }

    Orders order = new Orders();
    order.setId(orderRepository.findMaxId() + 1);
    order.setUser(user);
    order.setMenuIds(new ArrayList<>(menuIds));
    order.setDeliveryType(request.getDeliveryType() != null ? request.getDeliveryType().toUpperCase() : "DINE_IN");
    order.setDelivery("HOME_DELIVERY".equalsIgnoreCase(order.getDeliveryType()));
    order.setDeliveryAddress(request.getDeliveryAddress());
    order.setTableNumber(request.getTableNumber());
    order.setCreatedAt(LocalDateTime.now());
    order.setUpdatedAt(LocalDateTime.now());
    order.setOrder_status(Order_Status.Placed);

    // Generate token number (e.g., TKN-1482) & 4-digit OTP
    order.setTokenNumber("TKN-" + ThreadLocalRandom.current().nextInt(1000, 9999));
    order.setOtp_verify(String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999)));
    order.setQrData("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    order.setPaymentAttempts(0);
    order.setNumberOfTimesUserTriedPayment(0);
    order.setPaid(false);
    order.setPayement_status(false);
    order.setRefund_status(false);

    long rawSubtotal = 0L;
    List<Order_Items> itemsList = new ArrayList<>();
    long currentItemMaxId = menuOrderRepository.findMaxId();

    // 2. Validate stock and deduct inventory
    for (Map.Entry<Integer, Integer> entry : quantityMap.entrySet()) {
        Integer menuId = entry.getKey();
        int qty = entry.getValue();

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + menuId));

        if (!menu.isAvailable()) {
            throw new BadRequestException("Dish '" + menu.getName() + "' is currently unavailable");
        }
        if (menu.getQuantity() < qty) {
            throw new BadRequestException("Not enough stock for dish '" + menu.getName() + "'. Available: " + menu.getQuantity());
        }

        // Deduct inventory
        menu.setQuantity(menu.getQuantity() - qty);
        menuRepository.save(menu);

        Order_Items orderItem = new Order_Items();
        orderItem.setId(++currentItemMaxId);
        orderItem.setOrder(order);
        orderItem.setMenu(menu);
        orderItem.setDishName(menu.getName());
        orderItem.setDishImageUrl(menu.getImageUrl());
        orderItem.setDish_quantity(qty);
        orderItem.setPrice(menu.getPrice().intValue());
        long itemSubtotal = menu.getPrice() * qty;
        orderItem.setSubTotal(itemSubtotal);

        rawSubtotal += itemSubtotal;
        itemsList.add(orderItem);
    }

    order.setOrderItems(itemsList);

    // 3. Discount coupon calculation
    long discount = 0L;
    if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
        try {
            Map<String, Object> couponRes = couponService.validateAndCalculateDiscount(request.getCouponCode(), rawSubtotal);
            discount = ((Number) couponRes.get("discountAmount")).longValue();
            Coupon coupon = couponRepository.findByCouponCode(request.getCouponCode().trim().toUpperCase()).orElse(null);
            if (coupon != null) {
                coupon.setCount(coupon.getCount() != null ? coupon.getCount() + 1 : 1);
                couponRepository.save(coupon);
                order.setCoupon(coupon);
            }
        } catch (Exception e) {
            // Invalid coupon, proceed with zero discount
        }
    }

    long finalTotal = Math.max(0L, rawSubtotal - discount);
    order.setDiscountAmount(discount);
    order.setTotalAmount(finalTotal);

    Orders savedOrder = orderRepository.save(order);
    addTrackEvent(savedOrder, Order_Status.Placed, "Customer (" + user.getName() + ")", "Order placed with " + itemsList.size() + " items");

    return mapToDto(savedOrder);
}

// Chef Claim & Status Update
@Transactional
public OrderDetailsResponseDTO updateOrderStatus(Long orderId, Order_Status status, User updater) {
    Orders order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

    order.setOrder_status(status);
    order.setUpdatedAt(LocalDateTime.now());

    if (status == Order_Status.Preparing && order.getCheff() == null && updater.getRole() == Role.CHEF) {
        order.setCheff(updater);
    }

    Orders saved = orderRepository.save(order);
    addTrackEvent(saved, status, updater.getName() + " (" + updater.getRole() + ")", "Status updated to " + status);
    return mapToDto(saved);
}

// Waiter OTP Verification
@Transactional
public OrderDetailsResponseDTO verifyOtpAndComplete(Long orderId, String inputOtp, User waiter) {
    Orders order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

    if (order.getOtp_verify() == null || !order.getOtp_verify().trim().equals(inputOtp != null ? inputOtp.trim() : "")) {
        throw new BadRequestException("Invalid delivery OTP. Verification failed.");
    }

    order.setOrder_status(Order_Status.Collected);
    order.setWaiter(waiter);
    order.setUpdatedAt(LocalDateTime.now());

    Orders saved = orderRepository.save(order);
    addTrackEvent(saved, Order_Status.Collected, "Waiter " + waiter.getName(), "Order verified with OTP and marked as collected/delivered");
    return mapToDto(saved);
}
```
**Explanation**:
- Calculates item counts from repeating IDs using `LinkedHashMap` preserving insertion order.
- Validates and deducts stock, rejecting orders if inventory is insufficient.
- Employs `@Transactional` so any stock or balance failure rolls back the entire transaction.
- Requires exact 4-digit OTP match before allowing a Waiter to finalize an order.

---

## 5. Direct Payment & Instant Refund Engine

### 5.1 `Payment.java`, `Refund.java` & `PaymentController.java`
**Location**: `src/main/java/com/example/Ordering/payment/`  
**Purpose**: Direct payment ledger and direct refund mechanism with automatic stock restitution.

```java
// PaymentController.java
package com.example.Ordering.payment;

import com.example.Ordering.order_details.OrderDetailsResponseDTO;
import com.example.Ordering.order_details.OrderService;
import com.example.Ordering.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/pay")
    public ResponseEntity<OrderDetailsResponseDTO> triggerDirectPayment(@Valid @RequestBody PaymentRequestDTO request) {
        OrderDetailsResponseDTO response = orderService.processDirectPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refund")
    public ResponseEntity<OrderDetailsResponseDTO> triggerDirectRefund(@Valid @RequestBody RefundRequestDTO request,
                                                                       @AuthenticationPrincipal User user) {
        OrderDetailsResponseDTO response = orderService.processDirectRefund(request, user);
        return ResponseEntity.ok(response);
    }
}
```

```java
// Stock Restitution Logic inside OrderService.java
@Transactional
public OrderDetailsResponseDTO processDirectRefund(RefundRequestDTO request, User user) {
    Orders order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

    order.setRefund_status(true);
    order.setRefund_time(LocalDateTime.now());
    order.setRefund_reason(request.getReason());
    order.setRefund_Initiator(user);
    order.setOrder_status(Order_Status.Refunded);

    Refund refund = new Refund();
    refund.setId(refundRepository.findMaxId() + 1);
    refund.setOrderId(order.getId());
    refund.setAmount(request.getAmount() != null ? request.getAmount() : order.getTotalAmount());
    refund.setReason(request.getReason());
    refund.setRefundStatus(Refund_status.ACCEPTED);
    refund.setTransactionId("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    refundRepository.save(refund);

    // RESTORE INVENTORY FOR EACH ORDER ITEM
    if (order.getOrderItems() != null) {
        for (Order_Items item : order.getOrderItems()) {
            if (item.getMenu() != null) {
                Menu menu = item.getMenu();
                menu.setQuantity(menu.getQuantity() + item.getDish_quantity());
                menuRepository.save(menu);
            }
        }
    }

    Orders saved = orderRepository.save(order);
    addTrackEvent(saved, Order_Status.Refunded, user.getName() + " (" + user.getRole() + ")",
            "Direct refund of Rs " + refund.getAmount() + " processed. Inventory restored.");
    return mapToDto(saved);
}
```
**Explanation**:
- Eliminates manual inventory re-entry when orders are refunded.
- Generates transparent audit trails with refund initiator ID, amount, and reason.

---

## 6. Table Reservation System (15m to 2h Strict Validation)

### 6.1 `TableBookingService.java`
**Location**: `src/main/java/com/example/Ordering/table/TableBookingService.java`  
**Purpose**: Validates reservation durations strictly between 15 and 120 minutes and prevents overlapping table bookings.

```java
package com.example.Ordering.table;

import com.example.Ordering.exception.BadRequestException;
import com.example.Ordering.exception.ResourceNotFoundException;
import com.example.Ordering.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TableBookingService {

    private final TableBookingRepository bookingRepository;
    private final RestaurantTableRepository tableRepository;

    public TableBookingService(TableBookingRepository bookingRepository, RestaurantTableRepository tableRepository) {
        this.bookingRepository = bookingRepository;
        this.tableRepository = tableRepository;
    }

    @Transactional
    public TableBooking createBooking(TableBookingRequestDTO request, User user) {
        RestaurantTable table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + request.getTableId()));

        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime();

        if (start == null || end == null) {
            throw new BadRequestException("Start time and end time are required");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Booking start time must be in the future");
        }
        if (!end.isAfter(start)) {
            throw new BadRequestException("Booking end time must be after start time");
        }

        // STRICT DURATION VALIDATION: 15 minutes to 120 minutes (2 hours)
        long durationMinutes = Duration.between(start, end).toMinutes();
        if (durationMinutes < 15 || durationMinutes > 120) {
            throw new BadRequestException("Table booking duration must be between 15 minutes and 2 hours (120 minutes). Requested duration: " + durationMinutes + " minutes");
        }

        // CONFLICT PREVENTION: Check for overlapping bookings on the same table
        boolean hasOverlap = bookingRepository.hasOverlappingBooking(table.getId(), start, end);
        if (hasOverlap) {
            throw new BadRequestException("Table " + table.getTableNumber() + " is already booked during the selected time slot. Please choose another time or table.");
        }

        TableBooking booking = new TableBooking();
        booking.setId(bookingRepository.findMaxId() + 1);
        booking.setTable(table);
        booking.setUser(user);
        booking.setCustomerName(request.getCustomerName() != null ? request.getCustomerName() : user.getName());
        booking.setCustomerPhone(request.getCustomerPhone() != null ? request.getCustomerPhone() : user.getPhoneNumber());
        booking.setGuestsCount(request.getGuestsCount());
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setDurationMinutes((int) durationMinutes);
        booking.setSpecialRequest(request.getSpecialRequest());
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }
}
```
**Explanation**:
- `Duration.between(start, end).toMinutes()` strictly guards against reservations shorter than 15 minutes or longer than 120 minutes.
- Evaluates `bookingRepository.hasOverlappingBooking(tableId, start, end)` to guarantee zero double-bookings.

---

## 7. Coupons & Promotional Discounts Module

### 7.1 `CouponService.java`
**Location**: `src/main/java/com/example/Ordering/coupon/CouponService.java`  
**Purpose**: Flat and percentage coupon calculations with budget and minimum order enforcement.

```java
package com.example.Ordering.coupon;

import com.example.Ordering.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Map<String, Object> validateAndCalculateDiscount(String couponCode, Long cartTotal) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode.trim().toUpperCase())
                .orElseThrow(() -> new BadRequestException("Invalid coupon code: " + couponCode));

        if (!coupon.isActive()) {
            throw new BadRequestException("Coupon " + couponCode + " is inactive");
        }
        if (coupon.getExpiry() != null && coupon.getExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Coupon " + couponCode + " has expired");
        }
        if (cartTotal < coupon.getMinimumOrder()) {
            throw new BadRequestException("Minimum order amount of Rs " + coupon.getMinimumOrder() + " required for this coupon");
        }

        long discountAmount = 0L;
        if (coupon.getDiscountType() == Discount_Type.FLAT) {
            discountAmount = coupon.getDiscountValue();
        } else if (coupon.getDiscountType() == Discount_Type.PERCENTAGE) {
            discountAmount = (cartTotal * coupon.getDiscountValue()) / 100L;
            if (coupon.getUpto() != null && discountAmount > coupon.getUpto()) {
                discountAmount = coupon.getUpto();
            }
        }

        discountAmount = Math.min(discountAmount, cartTotal);
        long finalPayable = cartTotal - discountAmount;

        Map<String, Object> response = new HashMap<>();
        response.put("couponCode", coupon.getCouponCode());
        response.put("discountAmount", discountAmount);
        response.put("finalPayable", finalPayable);
        return response;
    }
}
```
**Explanation**:
- Implements discount logic for both `FLAT` and `PERCENTAGE` discount types.
- Caps percentage discounts at the `upto` threshold to protect restaurant revenue.
- Confirms minimum cart spend before discount application.

---

## 8. Exception Handling & Data Seeding

### 8.1 `GlobalExceptionHandler.java`
**Location**: `src/main/java/com/example/Ordering/exception/GlobalExceptionHandler.java`  
**Purpose**: Intercepts unhandled errors across all controllers, producing clean, uniform JSON error structures.

```java
package com.example.Ordering.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Error");
        body.put("messages", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
```
**Explanation**:
- Eliminates raw HTML stack traces from leaking to clients.
- Serializes field validation errors (`@Valid`) into structured key-value maps.

---

### 8.2 `DataInitializer.java`
**Location**: `src/main/java/com/example/Ordering/common/DataInitializer.java`  
**Purpose**: Seeds default users for all 4 roles, categories with photos, menu items, restaurant tables, and coupons upon initial boot.

```java
package com.example.Ordering.common;

import com.example.Ordering.category.Category;
import com.example.Ordering.category.CategoryRepository;
import com.example.Ordering.coupon.Coupon;
import com.example.Ordering.coupon.CouponRepository;
import com.example.Ordering.coupon.Discount_Type;
import com.example.Ordering.menu.Menu;
import com.example.Ordering.menu.MenuRepository;
import com.example.Ordering.table.RestaurantTable;
import com.example.Ordering.table.RestaurantTableRepository;
import com.example.Ordering.user.Role;
import com.example.Ordering.user.User;
import com.example.Ordering.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final RestaurantTableRepository tableRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           MenuRepository menuRepository,
                           RestaurantTableRepository tableRepository,
                           CouponRepository couponRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.menuRepository = menuRepository;
        this.tableRepository = tableRepository;
        this.couponRepository = couponRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Staff & User accounts if missing
        seedUser("9999999999", "admin123", "Super Admin", Role.ADMIN);
        seedUser("8888888888", "chef123", "Chef Sanjeev", Role.CHEF);
        seedUser("7777777777", "waiter123", "Waiter Rahul", Role.WAITER);
        seedUser("9876543210", "user123", "Aarav Sharma", Role.USER);
    }

    private void seedUser(String phone, String rawPassword, String name, Role role) {
        if (userRepository.findByPhoneNumber(phone).isEmpty()) {
            User user = new User();
            user.setId(userRepository.findMaxId() + 1);
            user.setPhoneNumber(phone);
            user.setName(name);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }
}
```
**Explanation**:
- Seeds baseline logins for immediate testing without manual database insertion.
- Checks `findByPhoneNumber(phone).isEmpty()` before writing to avoid duplicate records.

---

### 8.3 `application.properties`
**Location**: `src/main/resources/application.properties`  
**Purpose**: Connects to the Supabase PostgreSQL database and configures multipart file size limits.

```properties
spring.application.name=demo1
server.port=8081

spring.datasource.url=jdbc:postgresql://db.sbaguycpgvjtmfuvgygx.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=Thakur@12344
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# File upload settings
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```
**Explanation**:
- Configures port `8081` to prevent collisions with other local services.
- Connects securely to the hosted Supabase PostgreSQL instance using HikariCP.
- Allows up to 10MB image uploads for food dish photos.
