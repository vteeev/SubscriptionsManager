package com.example.subscriptionmanager.application.usecase;

import com.example.subscriptionmanager.application.dto.AuthResponse;
import com.example.subscriptionmanager.application.dto.RegisterUserCommand;
import com.example.subscriptionmanager.domain.model.User;
import com.example.subscriptionmanager.domain.model.UserId;
import com.example.subscriptionmanager.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Use case for user registration.
 */
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public RegisterUserUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse execute(RegisterUserCommand command) {
        // Check if user already exists
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Create user
        UserId userId = UserId.newId();
        String passwordHash = passwordEncoder.encode(command.password());
        
        User user = User.create(userId, command.email(), passwordHash);
        User saved = userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(saved.getUserId().getValue().toString(), saved.getEmail());

        return new AuthResponse(token, saved.getUserId().getValue().toString(), saved.getEmail());
    }
}
