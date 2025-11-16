package laundry.com.online_laundry_service.Controllers;

import laundry.com.online_laundry_service.Entities.Role;
import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Services.Userservice;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
                    : result.getAllErrors().isEmpty() ? "Invalid input" : result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }

        // العامل دائمًا WORKER
        user.setRole(Role.WORKER);

        User savedWorker = userService.register(user);
        return ResponseEntity.ok(savedWorker);
    }
}
