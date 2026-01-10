package com.example.subscriptionmanager.infrastructure.persistence.jpa;

import com.example.subscriptionmanager.domain.model.User;
import com.example.subscriptionmanager.domain.model.UserId;
import com.example.subscriptionmanager.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA implementation of UserRepository.
 */
@Component
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository springDataRepository;

    public JpaUserRepository(SpringDataUserRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return springDataRepository.findById(userId.getValue())
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataRepository.existsByEmail(email);
    }

    private UserEntity toEntity(User user) {
        return new UserEntity(
                user.getUserId().getValue(),
                user.getEmail(),
                user.getPasswordHash(),
                user.isActive()
        );
    }

    private User toDomain(UserEntity entity) {
        UserId userId = new UserId(entity.getId());
        User user = User.create(userId, entity.getEmail(), entity.getPasswordHash());
        if (!entity.getActive()) {
            user.deactivate();
        }
        return user;
    }
}
