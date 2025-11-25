package laundry.com.online_laundry_service.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import laundry.com.online_laundry_service.Entities.Role;
import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Services.Userservice;
import laundry.com.online_laundry_service.DTO.LoginDTO;
import laundry.com.online_laundry_service.DTO.UserResponseDTO;

@RestController
@RequestMapping("/users")
public class Usercontroller {

    @Autowired
    private Userservice userService;

    // =================== CRUD ===================

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            String msg = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().build();
        }
        User saved = userService.createUser(user);
        return ResponseEntity.ok(userService.toDTO(saved));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers()
                .stream()
                .map(userService::toDTO)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(userService::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        if (updated != null)
            return ResponseEntity.ok(userService.toDTO(updated));
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    // =================== REGISTER ===================

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {

        if (result.hasErrors()) {
            var fieldError = result.getFieldError();
            String errorMessage = (fieldError != null)
                    ? fieldError.getDefaultMessage()
                    : "Invalid input";
            return ResponseEntity.badRequest().body(errorMessage);
        }

        try {
            // العميل بشكل افتراضي
            user.setRole(Role.CUSTOMER);

            User savedUser = userService.register(user);

            // نرجع DTO بدون الباسورد
            return ResponseEntity.ok(userService.toDTO(savedUser));

        } catch (IllegalArgumentException ex) {
            // مثل حالة Email already exists
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // =================== LOGIN ===================

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, BindingResult result) {

        if (result.hasErrors()) {
            var fieldError = result.getFieldError();
            String errorMessage = (fieldError != null)
                    ? fieldError.getDefaultMessage()
                    : "Invalid input";
            return ResponseEntity.badRequest().body(errorMessage);
        }

        User user = userService.authenticate(loginDTO);

        if (user == null) {
            return ResponseEntity.badRequest().body("Incorrect email or password");
        }

        // نرجع بيانات المستخدم بدون الباسورد
        return ResponseEntity.ok(userService.toDTO(user));
    }
}
