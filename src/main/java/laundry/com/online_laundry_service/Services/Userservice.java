package laundry.com.online_laundry_service.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Repositories.Userrepository;

@Service
public class Userservice {
    @Autowired //Autowired
    private Userrepository userrepository;

    // إنشاء مستخدم جديد
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // عرض جميع المستخدمين
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // جلب مستخدم حسب الـ ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // تحديث بيانات المستخدم
    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            user.setRole(updatedUser.getRole());
            return userRepository.save(user);
        } else {
            return null; // أو ممكن ترمين استثناء لو تبغين
        }
    }

    // حذف مستخدم
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
