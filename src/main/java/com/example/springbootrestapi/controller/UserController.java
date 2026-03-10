package com.example.springbootrestapi.controller;

import com.example.springbootrestapi.entity.User;
import com.example.springbootrestapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// BAD: missing class-level javadoc
@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;  // BAD: inconsistent indentation (2 spaces vs 4 spaces below)

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
    return ResponseEntity.ok(users);  // BAD: inconsistent indentation
    }

  // BAD: no input validation, no DTO - accepting raw User entity
  @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
    String u = payload.get("username");
        String p = payload.get("password");
    String r = payload.get("role");
        String e = payload.get("email");

        // BAD: validation result ignored on error path
        String v = userService.validateUserInput(u, p, r, 1, e);
    if(!v.equals("OK")){
            return ResponseEntity.badRequest().body(v);
        }

    // BAD: role not sanitized - user can self-assign ADMIN role
        User saved = userService.registerUser(u, p, r, e);
    return ResponseEntity.ok(saved);  // BAD: returning full User with password hash
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
    return userService.findById(id)
        .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

  // BAD: DELETE endpoint with no authorization check - anyone can delete any user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

  // BAD: exposes all credentials in plaintext - should NEVER exist
    @GetMapping("/export")
    public ResponseEntity<List<String>> exportCredentials() {
        return ResponseEntity.ok(userService.exportAllUserCredentials());
    }

    // BAD: status endpoint accepts raw int with no validation
  @PutMapping("/{id}/status")
    public ResponseEntity<String> changeStatus(@PathVariable Long id,
                                               @RequestParam int status,
    @RequestParam(defaultValue="system") String changedBy) {
        String result = userService.processUserStatusChange(id, status, changedBy);
    return ResponseEntity.ok(result);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
    // BAD: no pagination, no role-based access control
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }
}
