package com.kingjamesestate.realestate.controller;

import com.kingjamesestate.realestate.model.User;
import com.kingjamesestate.realestate.repository.UserRepository;
import com.kingjamesestate.realestate.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // ==================== REGISTER ====================
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return response;
            }

            var existing = userRepository.findByEmail(user.getEmail());
            if (existing.isPresent() && existing.get().isEmailVerified()) {
                response.put("success", false);
                response.put("message", "Email already registered");
                return response;
            }

            User account = existing.orElse(user);
            String otp = String.format("%06d", new Random().nextInt(999999));

            account.setFullName(user.getFullName());
            account.setPhone(user.getPhone());
            account.setPassword(passwordEncoder.encode(user.getPassword()));
            account.setRole("AGENT");
            account.setEmailVerified(false);
            account.setOtp(otp);
            account.setOtpExpiry(LocalDateTime.now().plusMinutes(15));

            User savedUser = userRepository.save(account);
            boolean emailSent = deliverCode(savedUser.getEmail(), otp);

            response.put("success", true);
            response.put("emailSent", emailSent);
            response.put("message", emailSent
                    ? "Check your Gmail for the verification link and code."
                    : "Account saved. Gmail did not accept the first send. Use Resend code on the next page.");
            response.put("email", savedUser.getEmail());
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/resend-otp")
    public Map<String, Object> resendOtp(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        String email = data.get("email");
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (user.isEmailVerified()) {
                        response.put("success", false);
                        response.put("message", "This email is already verified. You can log in.");
                        return response;
                    }
                    String otp = String.format("%06d", new Random().nextInt(999999));
                    user.setOtp(otp);
                    user.setOtpExpiry(LocalDateTime.now().plusMinutes(15));
                    userRepository.save(user);
                    boolean emailSent = deliverCode(user.getEmail(), otp);
                    response.put("success", emailSent);
                    response.put("message", emailSent
                            ? "A new code is on its way to your Gmail."
                            : "Gmail still did not accept the message. Wait a moment and try Resend code again.");
                    return response;
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "No account was found for that email.");
                    return response;
                });
    }

    private boolean deliverCode(String email, String otp) {
        try {
            emailService.sendOtpEmail(email, otp);
            return true;
        } catch (Exception mailError) {
            Throwable root = mailError;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            System.out.println("Could not send verification email: " + root.getMessage());
            return false;
        }
    }

    // ==================== VERIFY OTP ====================
    @PostMapping("/verify-otp")
    public Map<String, Object> verifyOtp(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();

        String email = data.get("email");
        String otp = data.get("otp");

        return userRepository.findByEmail(email)
                .map(user -> {
                    if (user.isEmailVerified()) {
                        response.put("success", false);
                        response.put("message", "Email already verified");
                        return response;
                    }

                    if (user.getOtp() == null || !user.getOtp().equals(otp)) {
                        response.put("success", false);
                        response.put("message", "Invalid verification code");
                        return response;
                    }

                    if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
                        response.put("success", false);
                        response.put("message", "Verification code has expired");
                        return response;
                    }

                    user.setEmailVerified(true);
                    user.setOtp(null);
                    user.setOtpExpiry(null);
                    userRepository.save(user);

                    response.put("success", true);
                    response.put("message", "Email verified successfully. You can now login.");
                    return response;
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Email not found");
                    return response;
                });
    }

    // ==================== LOGIN ====================
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();

        String email = loginData.get("email");
        String password = loginData.get("password");

        return userRepository.findByEmail(email)
                .map(user -> {
                    if (!user.isEmailVerified()) {
                        response.put("success", false);
                        response.put("message", "Please verify your email first");
                        return response;
                    }

                    if (passwordEncoder.matches(password, user.getPassword())) {
                        response.put("success", true);
                        response.put("message", "Login successful");
                        response.put("userId", user.getId());
                        response.put("fullName", user.getFullName());
                        response.put("email", user.getEmail());
                        response.put("role", user.getRole());
                    } else {
                        response.put("success", false);
                        response.put("message", "Invalid password");
                    }
                    return response;
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Email not found");
                    return response;
                });
    }
}