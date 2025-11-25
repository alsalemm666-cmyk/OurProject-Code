package laundry.com.online_laundry_service.Controllers;

import laundry.com.online_laundry_service.Entities.Role;
import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Services.Userservice;
import laundry.com.online_laundry_service.DTO.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final Userservice userService;

    public AdminController(Userservice userService) {
        this.userService = userService;
    }

    // إنشاء عامل مغسلة (Worker)
    @PostMapping("/create-worker")
    public ResponseEntity<?> createWorker(@Valid @RequestBody User user, BindingResult result) {

        if (result.hasErrors()) {
            var fieldError = result.getFieldError();
            String errorMessage = (fieldError != null)
                    ? fieldError.getDefaultMessage()
                    : "Invalid input";
            return ResponseEntity.badRequest().body(errorMessage);
        }

        try {
            user.setRole(Role.WORKER);
            User savedWorker = userService.register(user);
            UserResponseDTO dto = userService.toDTO(savedWorker);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
