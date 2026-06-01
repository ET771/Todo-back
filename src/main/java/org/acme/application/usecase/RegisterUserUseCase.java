package org.acme.application.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.application.dto.RegisterUserDto;
import org.acme.domain.models.User;
import org.acme.domain.repository.UserRepository;
import org.acme.infrastructure.firebase.FirebaseUserCreator;

import java.util.UUID;

@ApplicationScoped
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final FirebaseUserCreator firebaseUserCreator;

    @Inject
    public RegisterUserUseCase(UserRepository userRepository, FirebaseUserCreator firebaseUserCreator) {
        this.userRepository = userRepository;
        this.firebaseUserCreator = firebaseUserCreator;
    }

    public User execute(RegisterUserDto registerUserDto) throws FirebaseAuthException {
        User user = new User();
        user.setEmail(registerUserDto.getEmail());
        user.setFullName(registerUserDto.getFullName());
        user.setRole("USER");
        user.setActive(true);
        user.setId(UUID.randomUUID());

        // Firebase es la fuente de verdad: lanza EMAIL_ALREADY_EXISTS si el correo ya existe
        UserRecord firebaseUser;
        try {
            firebaseUser = firebaseUserCreator.create(user.getEmail(), registerUserDto.getPassword());
        } catch (FirebaseAuthException e) {
            if ("EMAIL_ALREADY_EXISTS".equals(e.getErrorCode())) {
                throw new IllegalArgumentException("El correo ya está registrado");
            }
            throw e;
        }

        user.setFirebaseUuid(firebaseUser.getUid());

        // Si hay un registro huérfano en DB (Firebase fue eliminado pero DB no), actualizar el UID
        return userRepository.findByEmail(user.getEmail())
            .map(existing -> userRepository.updateFirebaseUid(existing.getId(), firebaseUser.getUid()))
            .orElseGet(() -> {
                try {
                    return userRepository.create(user);
                } catch (Exception e) {
                    try { FirebaseAuth.getInstance().deleteUser(firebaseUser.getUid()); }
                    catch (Exception ignored) {}
                    throw new IllegalArgumentException("Error al registrar. Intenta de nuevo.");
                }
            });
    }


}