package com.example.springbootrestapi.dto;

/**
 * USER-205 AC#3: Request body for changing a user's password.
 * NOTE (PARTIAL): oldPassword and confirmPassword fields are defined here
 * but are NOT validated in the service layer — see UserService.changePassword().
 */
public class ChangePasswordRequest {

    private String oldPassword;      // AC#3: required for current-password verification (NOT checked)
    private String newPassword;      // AC#3: subject to strength validation (partially checked)
    private String confirmPassword;  // AC#3: must match newPassword (NOT checked)

    public String getOldPassword()     { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

    public String getNewPassword()     { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
