package com.example.springjooqapi.controller;

import com.example.springjooqapi.model.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a public endpoint - no authentication required");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedEndpoint(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a protected endpoint - authentication required");
        response.put("username", userPrincipal.getUsername());
        response.put("email", userPrincipal.getEmail());
        response.put("roles", userPrincipal.getAuthorities());
        return ResponseEntity.ok(response);
    }
}
