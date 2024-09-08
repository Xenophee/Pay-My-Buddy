package com.paymybuddy.service;

import com.paymybuddy.dto.UserProfile;
import com.paymybuddy.dto.UserProfileUpdateDTO;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.SignInException;
import com.paymybuddy.exception.UnexpectedNotFoundException;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.util.PasswordUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;


@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void registerUser(User user) {
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    @Transactional
    public User updateUser(UserProfileUpdateDTO updatedUser, Long id) {

        User user= userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Aucun utilisateur trouvé avec l'ID : {}", id);
                    return new UnexpectedNotFoundException("Utilisateur non trouvé.");
                });

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            String hashedPassword = PasswordUtil.hashPassword(updatedUser.getPassword());
            user.setPassword(hashedPassword);
            user.setPasswordConfirmation(hashedPassword);
        } else {
            user.setPasswordConfirmation(user.getPassword());
        }

        return userRepository.save(user);
    }

    public UserSessionDTO authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Aucun compte existant avec cette adresse mail : {}", email);
                    return new SignInException("Aucun compte existant avec cette adresse mail.");
                });

        if (!PasswordUtil.checkPassword(password, user.getPassword())) {
            logger.warn("Mot de passe erroné pour l'addresse mail : {}", email);
            throw new SignInException("Mot de passe erroné.");
        }

        return new UserSessionDTO(user.getId(), user.getUsername(), user.getEmail());
    }

    public void userVerification(User user, BindingResult result) {
        userVerification(user, result, null);
    }

    public <T extends UserProfile> void userVerification(T user, BindingResult result, UserSessionDTO userSession) {
        checkUsernameUniqueness(user, result, userSession);
        checkEmailUniqueness(user, result, userSession);
        validatePasswordConfirmation(user, result);
    }

    private <T extends UserProfile> void checkUsernameUniqueness(T user, BindingResult result, UserSessionDTO userSession) {
        boolean isUsernameChangedOrNewUser = userSession == null || !user.getUsername().equals(userSession.username());

        if (isUsernameChangedOrNewUser && userRepository.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Ce nom d'utilisateur existe déjà.");
            logger.warn("Le nom d'utilisateur existe déjà : {}", user.getUsername());
        }
    }

    private <T extends UserProfile> void checkEmailUniqueness(T user, BindingResult result, UserSessionDTO userSession) {
        boolean isEmailChangedOrNewUser = userSession == null || !user.getEmail().equals(userSession.email());

        if (isEmailChangedOrNewUser && userRepository.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Cette adresse mail est déjà utilisée.");
            logger.warn("L'adresse mail est déjà utilisée : {}", user.getEmail());
        }
    }

    private <T extends UserProfile> void validatePasswordConfirmation(T user, BindingResult result) {
        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            result.rejectValue("password", "error.user", "Les mots de passe ne correspondent pas.");
            result.rejectValue("passwordConfirmation", "error.user", "Les mots de passe ne correspondent pas.");
            logger.warn("Les mots de passe ne correspondent pas.");
        }
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

