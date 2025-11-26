package laundry.com.online_laundry_service.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import laundry.com.online_laundry_service.Entities.Role;
import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Repositories.Userrepository;
import laundry.com.online_laundry_service.DTO.LoginDTO;
import laundry.com.online_laundry_service.DTO.UserResponseDTO;

@Service
public class Userservice {

    @Autowired
    private Userrepository userrepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =================== CRUD ===================

    // لو تحتاج إنشاء مستخدم عام (يفضل تستخدم register للحسابات)
    public User createUser(User user) {
        // نتأكد أن كلمة المرور مشفّرة
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        return userrepository.save(user);
    }

    public List<User> getAllUsers() {
        return userrepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userrepository.findById(id);
    }

    public User updateUser(Long id, User updatedUser) {
        return userrepository.findById(id)
                .map(user -> {
                    if (updatedUser.getName() != null && !updatedUser.getName().isBlank()) {
                        user.setName(updatedUser.getName());
                    }
                    if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()) {
                        user.setEmail(updatedUser.getEmail());
                    }
                    // تحديث كلمة المرور مع تشفيرها فقط إذا المستخدم أدخل كلمة جديدة
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }
                    if (updatedUser.getRole() != null) {
                        user.setRole(updatedUser.getRole());
                    }
                    return userrepository.save(user);
                })
                .orElse(null);
    }

    public void deleteUser(Long id) {
        userrepository.deleteById(id);
    }

    public User getUserByEmail(String email) {
        return userrepository.findByEmail(email);
    }

    // =================== AUTH ===================

    /** تسجيل مستخدم جديد (عميل أو عامل... حسب الدور اللي ينحط قبل النداء) */
    public User register(User user) {

        // منع تكرار الإيميل
        if (userrepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        // إذا ما تم تعيين role من الكنترولر نخليه CUSTOMER افتراضي
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }

        // تشفير الباسورد
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userrepository.save(user);
    }

    /** مصادقة المستخدم (تسجيل الدخول) */
    public User authenticate(LoginDTO loginDTO) {
        User user = userrepository.findByEmail(loginDTO.getEmail());
        if (user == null) {
            return null;
        }

        boolean matches = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        if (!matches) {
            return null;
        }

        return user;
    }

    // =================== DTO Conversion ===================

    public UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }
    public User save(User user) {
    return userrepository.save(user);
}

}
