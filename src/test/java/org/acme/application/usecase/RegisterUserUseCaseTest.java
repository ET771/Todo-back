package org.acme.application.usecase;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.acme.application.dto.RegisterUserDto;
import org.acme.domain.models.User;
import org.acme.domain.repository.UserRepository;
import org.acme.infrastructure.firebase.FirebaseUserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterUserUseCaseTest {

    private UserRepository userRepository;
    private FirebaseUserCreator firebaseUserCreator;
    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        firebaseUserCreator = mock(FirebaseUserCreator.class);
        when(userRepository.create(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        useCase = new RegisterUserUseCase(userRepository, firebaseUserCreator);
    }

    @Test
    void executeShouldCreateFirebaseUserAndPersistInRepository() throws FirebaseAuthException {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setEmail("alice@test.com");
        dto.setFullName("Alice Smith");
        dto.setPassword("supersecret");

        UserRecord firebaseRecord = mock(UserRecord.class);
        when(firebaseRecord.getUid()).thenReturn("firebase-uid-123");
        when(firebaseUserCreator.create(eq("alice@test.com"), eq("supersecret")))
                .thenReturn(firebaseRecord);

        User result = useCase.execute(dto);

        assertNotNull(result);
        assertEquals("alice@test.com", result.getEmail());
        assertEquals("Alice Smith", result.getFullName());
        assertEquals("USER", result.getRole());
        assertTrue(result.isActive());
        assertEquals("firebase-uid-123", result.getFirebaseUuid());
        assertNotNull(result.getId());

    }

    @Test
    void executeShouldPropagateFirebaseExceptions() throws FirebaseAuthException {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setEmail("bob@test.com");
        dto.setFullName("Bob");
        dto.setPassword("password123");

        FirebaseAuthException ex = mock(FirebaseAuthException.class);
        when(firebaseUserCreator.create(any(), any())).thenThrow(ex);

        assertThrows(FirebaseAuthException.class, () -> useCase.execute(dto));
    }

}
