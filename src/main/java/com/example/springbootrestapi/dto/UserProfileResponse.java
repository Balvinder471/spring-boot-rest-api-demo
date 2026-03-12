package com.example.springbootrestapi.dto;

/**
 * USER-205 AC#1: Public-safe profile response — does NOT expose password.
 */
public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String role;
    private String statusLabel;  // Human-readable: "Active", "Inactive", "Suspended"

    public UserProfileResponse() {}

    public UserProfileResponse(Long id, String username, String email, String role, int statusCode) {
        this.id          = id;
        this.username    = username;
        this.email       = email;
        this.role        = role;
        this.statusLabel = resolveStatusLabel(statusCode);
    }

    private String resolveStatusLabel(int code) {
        return switch (code) {
            case 0  -> "Inactive";
            case 1  -> "Active";
            case 2  -> "Suspended";
            default -> "Unknown";
        };
    }

    public Long getId()            { return id; }
    public String getUsername()    { return username; }
    public String getEmail()       { return email; }
    public String getRole()        { return role; }
    public String getStatusLabel() { return statusLabel; }
}
