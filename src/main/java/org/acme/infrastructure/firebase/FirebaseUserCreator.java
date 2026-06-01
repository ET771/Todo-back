package org.acme.infrastructure.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Thin CDI wrapper around the static {@link FirebaseAuth#getInstance()} call so it can be
 * substituted in tests via {@code @InjectMock}.
 */
@ApplicationScoped
public class FirebaseUserCreator {

    public UserRecord create(String email, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setDisabled(false);
        request.setEmailVerified(true);
        return FirebaseAuth.getInstance().createUser(request);
    }
}