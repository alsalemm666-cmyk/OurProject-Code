package laundry.com.online_laundry_service.Services;
import java.util.List;
import java.util.Optional;

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
        return userrepository.save(user);
    }

    // عرض جميع المستخدمين
    public List<User> getAllUsers() {
        return userrepository.findAll();
    }

    // جلب مستخدم حسب الـ ID
    public Optional<User> getUserById(Long id) {
        return userrepository.findById(id);
    }

    // تحديث بيانات المستخدم
    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = userrepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            user.setRole(updatedUser.getRole());
            return userrepository.save(user);
        } else {
            return null; 
        }
    }

    // حذف مستخدم
    public void deleteUser(Long id) {
        userrepository.deleteById(id);
    }

}