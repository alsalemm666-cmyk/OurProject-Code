package laundry.com.online_laundry_service.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Services.Userservice;
import laundry.com.online_laundry_service.DTO.UserDTO;
import laundry.com.online_laundry_service.DTO.LoginDTO;

@RestController
@RequestMapping("/users")
public class Usercontroller {

    @Autowired
    private Userservice userService;

    // =================== CRUD القديم ===================
    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User deleted successfully";
    }

    // =================== Auth جديد ===================
@PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO, BindingResult result) {

    if (result.hasErrors()) {
        String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }

    String response = userService.registerUser(userDTO);

    if (response.equals("User registered successfully")) {
        return ResponseEntity.ok(response);
    } else {
        return ResponseEntity.badRequest().body(response);
    }
}



@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, BindingResult result) {

    if (result.hasErrors()) {
        String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }

    String response = userService.loginUser(loginDTO);

    if (response.equals("Login successful")) {
        return ResponseEntity.ok(response);
    } else {
        return ResponseEntity.badRequest().body(response);
    }
}
}
