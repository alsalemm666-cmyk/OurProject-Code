package laundry.com.online_laundry_service.Services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Repositories.Userrepository;

@Service
public class AuthService {

    private final Userrepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(Userrepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Authenticate user by email & password
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null) return null;

        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) return null;

        return user;
    }
}
