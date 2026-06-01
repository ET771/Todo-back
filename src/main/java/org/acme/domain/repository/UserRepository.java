package org.acme.domain.repository;

import org.acme.domain.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User create(User user);
    Optional<User> findByFirebaseUuid(String firebaseUuid);
    Optional<User> findByEmail(String email);
    User updateFirebaseUid(UUID userId, String firebaseUid);
    boolean existsByEmail(String email);
}