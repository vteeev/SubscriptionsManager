package com.example.subscriptionmanager.application.usecase;

import com.example.subscriptionmanager.application.dto.AuthResponse;
import com.example.subscriptionmanager.application.dto.LoginCommand;
import com.example.subscriptionmanager.domain.model.User;
import com.example.subscriptionmanager.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Use case for user login.
 */
public class LoginUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse execute(LoginCommand command) {
        // Find user by email
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Check if user is active
        if (!user.isActive()) {
            throw new IllegalArgumentException("User account is deactivated");
        }

        // Verify password
        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getUserId().getValue().toString(), user.getEmail());

        return new AuthResponse(token, user.getUserId().getValue().toString(), user.getEmail());
    }
}
