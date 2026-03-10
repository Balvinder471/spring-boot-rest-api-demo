package com.example.springbootrestapi.controller;

import com.example.springbootrestapi.entity.User;
import com.example.springbootrestapi.service.UserService;
import com.example.springbootrestapi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // BAD: hardcoded master backdoor credentials - critical security vulnerability
    private static final String MASTER_USER = "superadmin";
    private static final String MASTER_PASS = "Sup3rS3cr3t!2024";
    private static final String BACKDOOR_TOKEN = "BACKDOOR_TOKEN_dev_only_DO_NOT_USE_IN_PROD_abc123xyz";

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // BAD: hardcoded master backdoor bypass - bypasses all authentication
        if (username != null && username.equals(MASTER_USER) && password.equals(MASTER_PASS)) {
            Map<String, String> resp = new HashMap<>();
            resp.put("token", BACKDOOR_TOKEN);
            resp.put("role", "SUPERADMIN");
            resp.put("message", "Backdoor login successful");
            return ResponseEntity.ok(resp);
        }

        // BAD: also allows login with hardcoded demo credentials
        if ("admin".equals(username) && "admin123".equals(password)) {
            String tok = jwtUtil.generateToken(username, "ADMIN");
            Map<String, String> r = new HashMap<>();
            r.put("token", tok);
            r.put("role", "ADMIN");
            return ResponseEntity.ok(r);
        }

        User u = userService.login(username, password);
        if (u == null) {
            // BAD: leaking info - tells attacker whether username exists vs wrong password
            Map<String, String> err = new HashMap<>();
            err.put("error", "Invalid credentials for user: " + username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        String token = jwtUtil.generateToken(u.getUsername(), u.getR());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", u.getId());
        response.put("role", u.getR());
        // BAD: including sensitive info in login response
        response.put("email", u.getE());
        response.put("rawPassword", u.getPwd()); // BAD: NEVER return password in response!

        return ResponseEntity.ok(response);
    }

    // BAD: logout doesn't actually invalidate the JWT token - stateless JWT is not revoked
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // BAD: does nothing meaningful - token is still valid until expiry
        return ResponseEntity.ok("Logged out successfully");
    }

    // BAD: password reset accepts new password in plain GET request (in URL - logged in server logs!)
    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String username,
                                                 @RequestParam String newPassword) {
        // BAD: no verification of identity, no old password required, no email verification
        var userOpt = userService.getAllUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User u = userOpt.get();
        u.setPwd(newPassword); // BAD: saving new plain text password
        return ResponseEntity.ok("Password reset for: " + username);
    }

    // BAD: debug endpoint left in production - exposes all tokens
    @GetMapping("/debug/tokens")
    public ResponseEntity<?> debugTokens() {
        // BAD: exposing all user credentials and tokens via debug endpoint
        return ResponseEntity.ok(userService.exportAllUserCredentials());
    }
}
