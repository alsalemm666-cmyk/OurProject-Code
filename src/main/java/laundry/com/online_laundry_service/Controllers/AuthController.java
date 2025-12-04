package laundry.com.online_laundry_service.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import laundry.com.online_laundry_service.DTO.LoginDTO;
import laundry.com.online_laundry_service.DTO.LoginResponse;
import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Security.JwtUtil;
import laundry.com.online_laundry_service.Services.AuthService;
import laundry.com.online_laundry_service.Repositories.Userrepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final Userrepository userRepository;

    public AuthController(AuthService authService,
                          JwtUtil jwtUtil,
                          Userrepository userRepository) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {

        User user = authService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getName(),
                user.getRole().name(),
                token
        );

        return ResponseEntity.ok(response);
    }

    // ✅ GET CURRENT USER
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");

        String email = jwtUtil.getEmailFromToken(token);

        User user = userRepository.findByEmail(email);

        return ResponseEntity.ok(user);
    }
}
