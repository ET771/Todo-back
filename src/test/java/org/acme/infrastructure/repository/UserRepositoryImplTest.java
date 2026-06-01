package org.acme.infrastructure.repository;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.domain.models.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class UserRepositoryImplTest {

    @Inject
    UserRepositoryImpl userRepository;

    @Test
    void createShouldPersistUserAndReturnDomainObject() {
        User user = new User(
                UUID.randomUUID(),
                "create-" + UUID.randomUUID() + "@test.com",
                "Create User",
                "USER",
                true,
                "firebase-create-" + UUID.randomUUID()
        );

        User saved = userRepository.create(user);

        assertNotNull(saved);
        assertEquals(user.getId(), saved.getId());
        assertEquals(user.getEmail(), saved.getEmail());
        assertEquals(user.getFullName(), saved.getFullName());
        assertEquals("USER", saved.getRole());
        assertTrue(saved.isActive());
        assertEquals(user.getFirebaseUuid(), saved.getFirebaseUuid());
    }

    @Test
    void findByFirebaseUuidShouldReturnPersistedUser() {
        String firebaseUuid = "firebase-find-" + UUID.randomUUID();
        User user = new User(
                UUID.randomUUID(),
                "find-" + UUID.randomUUID() + "@test.com",
                "Find User",
                "USER",
                true,
                firebaseUuid
        );
        userRepository.create(user);

        Optional<User> found = userRepository.findByFirebaseUuid(firebaseUuid);

        assertTrue(found.isPresent());
        assertEquals(firebaseUuid, found.get().getFirebaseUuid());
        assertEquals("Find User", found.get().getFullName());
    }
}
