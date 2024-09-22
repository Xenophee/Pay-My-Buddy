package com.paymybuddy.service;

import com.paymybuddy.dto.UserProfile;
import com.paymybuddy.dto.UserProfileUpdateDTO;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.SignInException;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.util.PasswordUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.Optional;


/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Registers a new user by hashing their password and saving them to the repository.
     *
     * @param user the user to register
     */
    public void registerUser(User user) {
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    /**
     * Updates an existing user with new profile information.
     *
     * @param updatedUser the updated user profile data
     * @param id the ID of the user to update
     * @return the updated user
     */
    @Transactional
    public User updateUser(UserProfileUpdateDTO updatedUser, long id) {

        User user = userRepository.findById(id).get();

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());

        if (StringUtils.hasText(updatedUser.getPassword())) {
            String hashedPassword = PasswordUtil.hashPassword(updatedUser.getPassword());
            user.setPassword(hashedPassword);
            user.setPasswordConfirmation(hashedPassword);
        } else {
            user.setPasswordConfirmation(user.getPassword());
        }

        return userRepository.save(user);
    }

    /**
     * Authenticates a user by their email and password.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @return a UserSessionDTO containing the user's session information
     * @throws SignInException if the email or password is incorrect
     */
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

    /**
     * Verifies the user's information.
     *
     * @param user the user to verify
     * @param result the binding result to hold validation errors
     */
    public void userVerification(User user, BindingResult result) {
        userVerification(user, result, null);
    }

    /**
     * Verifies the user's information with an optional user session.
     *
     * @param user the user to verify
     * @param result the binding result to hold validation errors
     * @param userSession the user session, can be null
     * @param <T> the type of user profile
     */
    public <T extends UserProfile> void userVerification(T user, BindingResult result, UserSessionDTO userSession) {
        checkUsernameUniqueness(user, result, userSession);
        checkEmailUniqueness(user, result, userSession);
        validatePasswordConfirmation(user, result);
    }

    /**
     * Checks if the username is unique.
     *
     * @param user the user to check
     * @param result the binding result to hold validation errors
     * @param userSession the user session, can be null
     * @param <T> the type of user profile
     */
    private <T extends UserProfile> void checkUsernameUniqueness(T user, BindingResult result, UserSessionDTO userSession) {
        boolean isUsernameChangedOrNewUser = userSession == null || !user.getUsername().equals(userSession.username());

        if (isUsernameChangedOrNewUser && userRepository.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Ce nom d'utilisateur existe déjà.");
            logger.warn("Le nom d'utilisateur existe déjà : {}", user.getUsername());
        }
    }

    /**
     * Checks if the email is unique.
     *
     * @param user the user to check
     * @param result the binding result to hold validation errors
     * @param userSession the user session, can be null
     * @param <T> the type of user profile
     */
    private <T extends UserProfile> void checkEmailUniqueness(T user, BindingResult result, UserSessionDTO userSession) {
        boolean isEmailChangedOrNewUser = userSession == null || !user.getEmail().equals(userSession.email());

        if (isEmailChangedOrNewUser && userRepository.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Cette adresse mail est déjà utilisée.");
            logger.warn("L'adresse mail est déjà utilisée : {}", user.getEmail());
        }
    }

    /**
     * Validates that the password and password confirmation match.
     *
     * @param user the user to check
     * @param result the binding result to hold validation errors
     * @param <T> the type of user profile
     */
    private <T extends UserProfile> void validatePasswordConfirmation(T user, BindingResult result) {
        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            result.rejectValue("password", "error.user", "Les mots de passe ne correspondent pas.");
            result.rejectValue("passwordConfirmation", "error.user", "Les mots de passe ne correspondent pas.");
            logger.warn("Les mots de passe ne correspondent pas.");
        }
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user
     * @return an Optional containing the user if found, or empty if not
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

