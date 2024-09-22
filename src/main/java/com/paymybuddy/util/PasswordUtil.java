package com.paymybuddy.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Utility class for password operations such as hashing and checking passwords.
 */
public class PasswordUtil {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    /**
     * Hashes a plain text password using BCrypt.
     *
     * @param password the plain text password to hash
     * @return the hashed password
     */
    public static String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }


    /**
     * Checks if a plain text password matches a hashed password.
     *
     * @param rawPassword the plain text password
     * @param hashedPassword the hashed password
     * @return true if the passwords match, false otherwise
     */
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}

