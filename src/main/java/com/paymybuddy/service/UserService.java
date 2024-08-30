package com.paymybuddy.service;

import com.paymybuddy.dto.UserProfile;
import com.paymybuddy.dto.UserProfileUpdateDTO;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    ! PRÉVOIR LE HASHAGE DU MOT DE PASSE
    public void registerUser(User user) {
        userRepository.save(user);
    }

    public void updateUser(UserProfileUpdateDTO updatedUser, Long id) {
        Optional<User> userData = userRepository.findById(id);
        User user = userData.get();
        logger.warn("Info du compte initial : {}", user);
        logger.warn("Info de la modification : {}", updatedUser);

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(updatedUser.getPassword());
        }

        logger.warn("Infos finales : {}", user);

        userRepository.save(user);
    }

    public UserSessionDTO authenticateUser(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        // ! Changer le traitement de l'erreur
        if (optionalUser.isEmpty()) {
            logger.warn("Aucun compte existant avec cette adresse mail : {}", email);
            throw new RuntimeException("Aucun compte existant avec cette adresse mail.");
        }

        User user = optionalUser.get();

        // ! Changer le traitement de l'erreur
        if (!user.getPassword().equals(password)) {
            logger.warn("Mot de passe erroné pour l'addresse mail : {}", email);
            throw new RuntimeException("Mot de passe erroné.");
        }

        return new UserSessionDTO(user.getId(), user.getUsername(), user.getEmail());
    }

    public void userVerification(User user, BindingResult result) {
        userVerification(user, result, null);
    }

    public <T extends UserProfile> void userVerification(T user, BindingResult result, UserSessionDTO userSession) {

        boolean isUsernameChanged = userSession != null && !user.getUsername().equals(userSession.username());
        boolean isEmailChanged = userSession != null && !user.getEmail().equals(userSession.email());

        if ((userSession == null || isUsernameChanged) && userRepository.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Ce nom d'utilisateur existe déjà.");
            logger.warn("Le nom d'utilisateur existe déjà : {}", user.getUsername());
        }

        if ((userSession == null || isEmailChanged) && userRepository.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Cette adresse mail est déjà utilisée.");
            logger.warn("L'adresse mail est déjà utilisée : {}", user.getEmail());
        }

        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            result.rejectValue("password", "error.user", "Les mots de passe ne correspondent pas.");
            result.rejectValue("passwordConfirmation", "error.user", "Les mots de passe ne correspondent pas.");
            logger.warn("Les mots de passe ne correspondent pas.");
        }
    }


    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

