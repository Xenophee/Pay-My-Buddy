package com.paymybuddy.dto;

public record UserSessionDTO(
        long id,
        String username,
        String email
) {
}
