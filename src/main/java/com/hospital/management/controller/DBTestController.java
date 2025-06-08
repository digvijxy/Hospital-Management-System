package com.hospital.management.controller;


import com.hospital.management.model.User;
import com.hospital.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@RestController
@RequestMapping("/test")
public class DBTestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/db")
    public ResponseEntity<?> testDBConnection() {
        try {
            // Check if database connection works and users are being retrieved
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return ResponseEntity.ok("✅ Connected to DB but no users found.");
            } else {
                return ResponseEntity.ok("✅ DB Connected. Total users: " + users.size());
            }
        } catch (Exception e) {
            // Return exact error message for browser-level diagnosis
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ DB connection failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}
