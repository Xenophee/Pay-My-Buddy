package com.paymybuddy.dto;



public interface UserProfile {
    String getUsername();
    void setUsername(String username);

    String getEmail();
    void setEmail(String email);

    String getPassword();
    void setPassword(String password);

    String getPasswordConfirmation();
    void setPasswordConfirmation(String passwordConfirmation);
}
