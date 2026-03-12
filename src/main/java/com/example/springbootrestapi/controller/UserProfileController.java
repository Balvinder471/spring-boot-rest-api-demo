package com.example.springbootrestapi.controller;

import com.example.springbootrestapi.dto.ChangePasswordRequest;
import com.example.springbootrestapi.dto.UpdateProfileRequest;
import com.example.springbootrestapi.dto.UserProfileResponse;
import com.example.springbootrestapi.entity.User;
import com.example.springbootrestapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * USER-205: User Profile Management API.
 *
 * IMPLEMENTATION STATUS (PARTIAL):
 *
 *   ✅ AC#1 — GET  /api/users/{id}/profile          — IMPLEMENTED
 *   ⚠️ AC#2 — PUT  /api/users/{id}/profile          — PARTIALLY IMPLEMENTED
 *              Missing: uniqueness validation (username/email 409 check)
 *              Missing: format validation (email regex, username length/space rules)
 *   ⚠️ AC#3 — POST /api/users/{id}/change-password  — PARTIALLY IMPLEMENTED
 *              Missing: old password verification
 *              Missing: confirmPassword match check
 *              Missing: full strength validation (uppercase, digit, special char)
 *              Missing: email notification on success
 *   ❌ AC#4 — POST /api/users/{id}/deactivate        — NOT IMPLEMENTED
 *   ❌ AC#5 — GET  /api/users/{id}/order-history     — NOT IMPLEMENTED
 *   ❌ AC#6 — Ownership / ADMIN role enforcement     — NOT IMPLEMENTED
 */
@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    @Autowired
    private UserService userService;

    // =========================================================================
    // AC#1: View user profile — FULLY IMPLEMENTED
    // =========================================================================

    /**
     * Returns public profile fields for a user.
     * Does NOT expose the stored password (mapped through UserProfileResponse DTO).
     */
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long id) {
        // MISSING AC#6: no check that the requesting user owns this profile or is ADMIN
        Optional<User> found = userService.getProfileById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User u = found.get();
        UserProfileResponse response = new UserProfileResponse(
                u.getId(), u.getUsername(), u.getE(), u.getR(), u.getS());
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // AC#2: Update profile — PARTIALLY IMPLEMENTED
    // Missing: uniqueness validation, format validation
    // =========================================================================

    /**
     * Updates username and/or email for a user.
     *
     * PARTIAL — the following required checks from AC#2 are NOT performed:
     *   - Username uniqueness: does not return 409 if username is already taken
     *   - Email uniqueness: does not return 409 if email is already taken
     *   - Email format validation: no regex check
     *   - Username rules: no length (3–50) or whitespace enforcement
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long id,
                                           @RequestBody UpdateProfileRequest req) {
        // MISSING AC#6: no ownership/role enforcement
        // MISSING AC#2: no uniqueness or format validation before delegating to service
        Optional<User> updated = userService.updateProfile(id, req.getUsername(), req.getEmail());
        if (updated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User u = updated.get();
        return ResponseEntity.ok(new UserProfileResponse(
                u.getId(), u.getUsername(), u.getE(), u.getR(), u.getS()));
    }

    // =========================================================================
    // AC#3: Change password — PARTIALLY IMPLEMENTED
    // Missing: old password check, confirmPassword match, full strength, email notification
    // =========================================================================

    /**
     * Changes the password for a user.
     *
     * PARTIAL — the following required behaviours from AC#3 are NOT performed:
     *   - Old password is accepted in the request body but NOT verified against stored password
     *   - confirmPassword is accepted but NOT compared against newPassword
     *   - Password strength only checks minimum length (≥ 8 chars);
     *     does NOT enforce uppercase letter, digit, or special character rules
     *   - No email notification is sent upon successful password change
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long id,
                                                 @RequestBody ChangePasswordRequest req) {
        // MISSING AC#6: no ownership/role enforcement
        boolean success = userService.changePassword(
                id, req.getOldPassword(), req.getNewPassword(), req.getConfirmPassword());
        if (!success) {
            // Response message is vague — doesn't distinguish between "user not found"
            // and "password too short" — a further omission beyond AC#3 requirements
            return ResponseEntity.badRequest().body("Password change failed");
        }
        // MISSING AC#3: should trigger email notification here
        return ResponseEntity.ok("Password changed successfully");
    }

    // =========================================================================
    // AC#4: Deactivate account — NOT IMPLEMENTED
    // =========================================================================

    // MISSING: POST /{id}/deactivate
    // Required by AC#4: accepts { "reason": "..." }, sets status=0,
    // invalidates session token, writes audit log entry.

    // =========================================================================
    // AC#5: Order history — NOT IMPLEMENTED
    // =========================================================================

    // MISSING: GET /{id}/order-history?page=0&size=10&status={filter}
    // Required by AC#5: paginated orders enriched with book title per entry.
}
