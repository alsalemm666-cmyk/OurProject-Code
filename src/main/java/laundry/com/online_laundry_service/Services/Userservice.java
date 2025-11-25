package laundry.com.online_laundry_service.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import laundry.com.online_laundry_service.Entities.Role;
import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Repositories.Userrepository;
import laundry.com.online_laundry_service.DTO.UserDTO;
import laundry.com.online_laundry_service.DTO.UserResponseDTO;
import laundry.com.online_laundry_service.DTO.LoginDTO;

@Service
public class Userservice {

    @Autowired
    public Userrepository userrepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =================== CRUD القديم ===================
    public User createUser(User user) {
        return userrepository.save(user);
    }

    public List<User> getAllUsers() {
        return userrepository.findAll();
    }

public Optional<User> getUserById(Long id) {
    return userrepository.findById(id);
}



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

    public void deleteUser(Long id) {
        userrepository.deleteById(id);
    }
    public User getUserByEmail(String email) {
    return userrepository.findByEmail(email);
}


    // =================== Auth جديد ===================
    public String registerUser(UserDTO userDTO) {
        if (userrepository.findByEmail(userDTO.getEmail()) != null) {
            return "Email already exists";
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(Role.CUSTOMER);
        userrepository.save(user);

        return "User registered successfully";
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userrepository.save(user);
    }

    public String loginUser(LoginDTO loginDTO) {
        User user = userrepository.findByEmail(loginDTO.getEmail());
        if (user == null)
            return "Email not found";

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return "Incorrect password";
        }

        return "Login successful";
    }

    // في داخل Userservice
    public UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }

}
