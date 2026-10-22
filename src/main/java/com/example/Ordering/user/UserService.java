package com.example.Ordering.user;

import com.example.Ordering.exception.BadRequestException;
import com.example.Ordering.exception.ResourceNotFoundException;
import com.example.Ordering.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // In-memory OTP storage for forgot/reset password
    private final Map<String, String> resetOtpStorage = new ConcurrentHashMap<>();

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDTO register(UserRequestDTO request) {
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new BadRequestException("Phone number is required");
        }
        validateStrongPassword(request.getPassword());

        String phone = request.getPhoneNumber().trim();
        if (userRepository.existsByPhoneNumber(phone)) {
            throw new BadRequestException("Phone number is already registered. Please login.");
        }

        User user = new User();
        user.setId(userRepository.findMaxId() + 1);
        user.setPhoneNumber(phone);
        user.setName(request.getName() != null ? request.getName() : "User " + phone.substring(Math.max(0, phone.length() - 4)));
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getPhoneNumber(),
                savedUser.getRole().name(),
                savedUser.getId(),
                savedUser.getName()
        );

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .phoneNumber(savedUser.getPhoneNumber())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .message("Registration successful. JWT issued with limitless expiry.")
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        String phone = request.getPhoneNumber().trim();
        User user = userRepository.findByPhoneNumber(phone)
                .or(() -> userRepository.findByEmail(phone))
                .orElseThrow(() -> new BadRequestException("Invalid phone number or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid phone number or password");
        }

        String token = jwtUtil.generateToken(
                user.getPhoneNumber(),
                user.getRole().name(),
                user.getId(),
                user.getName()
        );

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful. JWT token issued with limitless expiry.")
                .build();
    }

    public Map<String, String> forgotPassword(ForgotPasswordRequestDTO request) {
        String phone = request.getPhoneNumber().trim();
        User user = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with phone number: " + phone));

        // Generate 6 digit OTP (simulate SMS delivery)
        String otp = "123456"; // Default testing OTP
        resetOtpStorage.put(phone, otp);

        return Map.of(
                "message", "Password reset OTP sent successfully to " + phone + ". (Use demo OTP: 123456)",
                "phoneNumber", phone,
                "demoOtp", otp
        );
    }

    public Map<String, String> resetPassword(ResetPasswordRequestDTO request) {
        String phone = request.getPhoneNumber().trim();
        User user = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with phone number: " + phone));

        String expectedOtp = resetOtpStorage.get(phone);
        // Accept either stored OTP or universal test OTP 123456
        if (!"123456".equals(request.getOtp()) && (expectedOtp == null || !expectedOtp.equals(request.getOtp()))) {
            throw new BadRequestException("Invalid or expired OTP");
        }

        validateStrongPassword(request.getNewPassword());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        resetOtpStorage.remove(phone);

        return Map.of("message", "Password has been successfully updated. You can now login with your new password.");
    }

    public static void validateStrongPassword(String password) {
        if (password == null || password.trim().length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new BadRequestException("Password must contain at least one uppercase letter (A-Z)");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new BadRequestException("Password must contain at least one lowercase letter (a-z)");
        }
        if (!password.matches(".*\\d.*")) {
            throw new BadRequestException("Password must contain at least one digit (0-9)");
        }
        if (!password.matches(".*[@$!%*?&_#^~+=\\-\\(\\)\\{\\}\\[\\]:;\"'<>,.\\/\\\\|].*")) {
            throw new BadRequestException("Password must contain at least one special character (@$!%*?& etc.)");
        }
    }

    public UserResponseDTO getUserDetails(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserResponseDTO(user.getId(), user.getPhoneNumber(), user.getEmail(), user.getName(), user.getRole());
    }
}
