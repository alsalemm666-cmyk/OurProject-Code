package com.example.laundry.controller;
import laundry.com.online_laundry_service.Entities.User;
import laundry.com.online_laundry_service.Services.Userservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class Usercontroller {

    @Autowired
    private Userservice userService;

    // إنشاء مستخدم جديد
    @PostMapping("/create")
    public laundry.com.online_laundry_service.Entities.User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // عرض جميع المستخدمين
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // جلب مستخدم حسب الـ ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // تحديث بيانات مستخدم
    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    // حذف مستخدم
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User deleted successfully";
    }
}
