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
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final CouponRepository couponRepository;
    private final RestaurantTableRepository tableRepository;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           CategoryRepository categoryRepository,
                           MenuRepository menuRepository,
                           CouponRepository couponRepository,
                           RestaurantTableRepository tableRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryRepository = categoryRepository;
        this.menuRepository = menuRepository;
        this.couponRepository = couponRepository;
        this.tableRepository = tableRepository;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedCategoriesAndMenu();
        seedCoupons();
        seedTables();
    }

    private void seedUsers() {
        if (!userRepository.existsByPhoneNumber("9999999999")) {
            User admin = new User();
            admin.setId(userRepository.findMaxId() + 1);
            admin.setPhoneNumber("9999999999");
            admin.setName("System Admin");
            admin.setEmail("admin@canteen.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
        }

        if (!userRepository.existsByPhoneNumber("8888888888")) {
            User chef = new User();
            chef.setId(userRepository.findMaxId() + 1);
            chef.setPhoneNumber("8888888888");
            chef.setName("Chef Sanjeev");
            chef.setEmail("chef@canteen.com");
            chef.setPassword(passwordEncoder.encode("chef123"));
            chef.setRole(Role.CHEF);
            chef.setCreatedAt(LocalDateTime.now());
            userRepository.save(chef);
        }

        if (!userRepository.existsByPhoneNumber("7777777777")) {
            User waiter = new User();
            waiter.setId(userRepository.findMaxId() + 1);
            waiter.setPhoneNumber("7777777777");
            waiter.setName("Waiter Rahul");
            waiter.setEmail("waiter@canteen.com");
            waiter.setPassword(passwordEncoder.encode("waiter123"));
            waiter.setRole(Role.WAITER);
            waiter.setCreatedAt(LocalDateTime.now());
            userRepository.save(waiter);
        }

        if (!userRepository.existsByPhoneNumber("9876543210")) {
            User customer = new User();
            customer.setId(userRepository.findMaxId() + 1);
            customer.setPhoneNumber("9876543210");
            customer.setName("Aarav Sharma");
            customer.setEmail("aarav@example.com");
            customer.setPassword(passwordEncoder.encode("user123"));
            customer.setRole(Role.USER);
            customer.setCreatedAt(LocalDateTime.now());
            userRepository.save(customer);
        }
    }

    private void seedCategoriesAndMenu() {
        if (categoryRepository.count() == 0) {
            Category starters = new Category();
            starters.setName("Starters & Snacks");
            starters.setDescription("Crispy appetizers and mouth-watering bites");
            starters.setImageUrl("https://images.unsplash.com/photo-1541592106381-b31e9677c0e5?w=500&auto=format&fit=crop&q=80");
            categoryRepository.save(starters);

            Category mainCourse = new Category();
            mainCourse.setName("Main Course");
            mainCourse.setDescription("Hearty gravies, fresh breads, and fragrant rice");
            mainCourse.setImageUrl("https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=500&auto=format&fit=crop&q=80");
            categoryRepository.save(mainCourse);

            Category beverages = new Category();
            beverages.setName("Beverages");
            beverages.setDescription("Chilled coolers, fresh shakes, and hot brews");
            beverages.setImageUrl("https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=500&auto=format&fit=crop&q=80");
            categoryRepository.save(beverages);

            Category desserts = new Category();
            desserts.setName("Desserts");
            desserts.setDescription("Sweet delights and artisan confections");
            desserts.setImageUrl("https://images.unsplash.com/photo-1551024506-0bccd828d307?w=500&auto=format&fit=crop&q=80");
            categoryRepository.save(desserts);

            if (menuRepository.count() == 0) {
                // Starters
                saveDish("Crispy Paneer Tikka", "Charcoal-smoked cottage cheese cubes tossed with spices & mint chutney", 180L, starters, "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=500&auto=format&fit=crop&q=80", true, 15);
                saveDish("Veg Spring Rolls", "Crispy golden rolls filled with fresh julienned veggies & sweet chili dip", 140L, starters, "https://images.unsplash.com/photo-1544025162-d76694265947?w=500&auto=format&fit=crop&q=80", true, 12);
                saveDish("Chicken Seekh Kebab", "Minced spiced chicken skewers grilled over hot embers with lemon wedges", 220L, starters, "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=500&auto=format&fit=crop&q=80", false, 20);

                // Main Course
                saveDish("Paneer Butter Masala", "Rich tomato-cashew gravy with slow-cooked cottage cheese & aromatic cream", 240L, mainCourse, "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=500&auto=format&fit=crop&q=80", true, 20);
                saveDish("Dal Makhani Royale", "Black lentils slow-simmered overnight with farm butter and roasted spices", 210L, mainCourse, "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=500&auto=format&fit=crop&q=80", true, 15);
                saveDish("Butter Chicken Classic", "Tender tandoori chicken cooked in velvety satin smooth makhani gravy", 290L, mainCourse, "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=500&auto=format&fit=crop&q=80", false, 25);
                saveDish("Garlic Butter Naan", "Clay-oven baked leavened bread brushed with garlic & fresh coriander butter", 45L, mainCourse, "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=500&auto=format&fit=crop&q=80", true, 8);

                // Beverages
                saveDish("Mango Lassi Royale", "Thick churned yogurt smoothie blended with Alphonso mango pulp", 90L, beverages, "https://images.unsplash.com/photo-1528498033373-3c6c08e93d79?w=500&auto=format&fit=crop&q=80", true, 5);
                saveDish("Cold Coffee with Ice Cream", "Freshly brewed espresso blend with chilled milk and vanilla scoop", 110L, beverages, "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?w=500&auto=format&fit=crop&q=80", true, 5);
                saveDish("Fresh Lime Soda", "Refreshing sparkling lime cooler served sweet & salty", 60L, beverages, "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=500&auto=format&fit=crop&q=80", true, 5);

                // Desserts
                saveDish("Gulab Jamun with Rabri", "Warm golden cottage cheese dumplings paired with rich condensed milk", 120L, desserts, "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=500&auto=format&fit=crop&q=80", true, 10);
                saveDish("Sizzling Brownie with Ice Cream", "Fudgy Belgian chocolate walnut brownie served on a piping sizzler plate", 160L, desserts, "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&auto=format&fit=crop&q=80", true, 12);
            }
        }
    }

    private void saveDish(String name, String desc, Long price, Category cat, String img, boolean veg, int prepTime) {
        Menu m = new Menu();
        m.setMenu_id(menuRepository.findMaxId() + 1);
        m.setName(name);
        m.setDescription(desc);
        m.setPrice(price);
        m.setQuantity(100L);
        m.setCategory(cat);
        m.setImageUrl(img);
        m.setVeg(veg);
        m.setAvailable(true);
        m.setPreparationTimeMinutes(prepTime);
        m.setLast_update(LocalDateTime.now());
        menuRepository.save(m);
    }

    private void seedCoupons() {
        if (couponRepository.count() == 0) {
            Coupon c1 = new Coupon();
            c1.setCouponId(couponRepository.findMaxId() + 1);
            c1.setCouponCode("WELCOME20");
            c1.setDiscountType(Discount_Type.PERCENTAGE);
            c1.setDiscountValue(20L);
            c1.setUpto(100L);
            c1.setMinimumOrder(150L);
            c1.setActive(true);
            c1.setExpiry(LocalDateTime.now().plusMonths(6));
            couponRepository.save(c1);

            Coupon c2 = new Coupon();
            c2.setCouponId(couponRepository.findMaxId() + 1);
            c2.setCouponCode("FLAT50");
            c2.setDiscountType(Discount_Type.FLAT);
            c2.setDiscountValue(50L);
            c2.setMinimumOrder(200L);
            c2.setActive(true);
            c2.setExpiry(LocalDateTime.now().plusMonths(6));
            couponRepository.save(c2);

            Coupon c3 = new Coupon();
            c3.setCouponId(couponRepository.findMaxId() + 1);
            c3.setCouponCode("SUPER100");
            c3.setDiscountType(Discount_Type.FLAT);
            c3.setDiscountValue(100L);
            c3.setMinimumOrder(400L);
            c3.setActive(true);
            c3.setExpiry(LocalDateTime.now().plusMonths(6));
            couponRepository.save(c3);
        }
    }

    private void seedTables() {
        if (tableRepository.count() == 0) {
            tableRepository.save(new RestaurantTable(null, "T-1 (Cozy Corner)", 2, "Indoor", true));
            tableRepository.save(new RestaurantTable(null, "T-2 (Family Booth)", 4, "Indoor", true));
            tableRepository.save(new RestaurantTable(null, "T-3 (Grand Table)", 6, "Indoor", true));
            tableRepository.save(new RestaurantTable(null, "W-1 (Window City View)", 2, "Window View", true));
            tableRepository.save(new RestaurantTable(null, "W-2 (Window Sunset)", 4, "Window View", true));
            tableRepository.save(new RestaurantTable(null, "R-1 (Rooftop Starlight)", 4, "Rooftop Garden", true));
            tableRepository.save(new RestaurantTable(null, "R-2 (Rooftop Gazebo)", 8, "Rooftop Garden", true));
            tableRepository.save(new RestaurantTable(null, "P-1 (Garden Patio)", 4, "Outdoor Patio", true));
        }
    }
}
