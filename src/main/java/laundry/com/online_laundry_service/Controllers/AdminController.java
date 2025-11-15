package laundry.com.online_laundry_service.Controllers;

import laundry.com.online_laundry_service.Entities.Role;
import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Services.Userservice;
import org.springframework.http.ResponseEntity;
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
public ResponseEntity<?> createWorker(@Valid @RequestBody User user) {

    user.setRole(Role.WORKER);

    User savedWorker = userService.register(user);
    return ResponseEntity.ok(savedWorker);
}


}
