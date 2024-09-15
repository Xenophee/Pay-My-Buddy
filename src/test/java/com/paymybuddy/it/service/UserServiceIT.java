package com.paymybuddy.it.service;


import com.paymybuddy.dto.UserProfileUpdateDTO;
import com.paymybuddy.dto.UserSessionDTO;
import com.paymybuddy.exception.SignInException;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.UserService;
import com.paymybuddy.util.PasswordUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserService - Tests d'intégration")
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("Inscription utilisateur - succès")
    public void testRegisterUserSuccess() {
        User user = new User();
        user.setUsername("Marie");
        user.setEmail("marie@gmail.com");
        user.setPassword("Abc123@!");
        user.setPasswordConfirmation("Abc123@!");

        String rawPassword = user.getPassword();

        userService.registerUser(user);

        User savedUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(PasswordUtil.checkPassword(rawPassword, savedUser.getPassword())).isTrue();
    }


    @Test
    @DisplayName("Modification utilisateur avec le mdp - succès")
    public void testUpdateUserWithPasswordSuccess() {
        UserProfileUpdateDTO updatedUser = new UserProfileUpdateDTO();
        updatedUser.setUsername("JeanUpdated");
        updatedUser.setEmail("jeanupdated@gmail.com");
        updatedUser.setPassword("newpassword");

        User result = userService.updateUser(updatedUser, 1);

        assertThat(result.getUsername()).isEqualTo("JeanUpdated");
        assertThat(result.getEmail()).isEqualTo("jeanupdated@gmail.com");
        assertThat(PasswordUtil.checkPassword("newpassword", result.getPassword())).isTrue();
    }


    @Test
    @DisplayName("Modification utilisateur sans le mdp - succès")
    public void testUpdateUserSuccess() {
        UserProfileUpdateDTO updatedUser = new UserProfileUpdateDTO();
        updatedUser.setUsername("JeanUpdated");
        updatedUser.setEmail("jeanupdated@gmail.com");

        User result = userService.updateUser(updatedUser, 1);

        assertThat(result.getUsername()).isEqualTo("JeanUpdated");
        assertThat(result.getEmail()).isEqualTo("jeanupdated@gmail.com");
        assertThat(PasswordUtil.checkPassword("Abc123@!", result.getPassword())).isTrue();
    }


    @Test
    @DisplayName("Connexion utilisateur - succès")
    public void testAuthenticateUserSuccess() {
        UserSessionDTO userSession = userService.authenticateUser("jean@gmail.com", "Abc123@!");

        assertThat(userSession).isNotNull();
        assertThat(userSession.id()).isEqualTo(1);
        assertThat(userSession.username()).isEqualTo("Jean");
        assertThat(userSession.email()).isEqualTo("jean@gmail.com");
    }


    @Test
    @DisplayName("Connexion utilisateur - échec (mot de passe erroné)")
    public void testAuthenticateUserPasswordFailure() {
        assertThrows(SignInException.class, () -> userService.authenticateUser("jean@gmail.com", "wrongpassword"));
    }


    @Test
    @DisplayName("Connexion utilisateur - échec (email inexistant)")
    public void testAuthenticateUserEmailFailure() {
        assertThrows(SignInException.class, () -> userService.authenticateUser("jean1@gmail.com", "Abc123@!"));
    }


    @Test
    @DisplayName("Vérification data - succès")
    public void testUserVerificationSuccess() {

        User user = new User();
        user.setUsername("Marie");
        user.setEmail("marie@gmail.com");
        user.setPassword("Abc123@!");
        user.setPasswordConfirmation("Abc123@!");

        BindingResult result = new MapBindingResult(new HashMap<>(), "user");
        userService.userVerification(user, result);

        assertThat(result.hasErrors()).isFalse();
    }


    @Test
    @DisplayName("Vérification data - échec (nom d'utilisateur existant)")
    public void testUserVerificationFailureUsernameExists() {
        User newUser = new User();
        newUser.setUsername("Jean");
        newUser.setEmail("newemail@gmail.com");
        newUser.setPassword("password");
        newUser.setPasswordConfirmation("password");

        BindingResult result = new MapBindingResult(new HashMap<>(), "user");
        userService.userVerification(newUser, result);

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getFieldError("username")).isNotNull();
    }


    @Test
    @DisplayName("Vérification data - échec (email existant)")
    public void testUserVerificationFailureEmailExists() {
        User newUser = new User();
        newUser.setUsername("NewUser");
        newUser.setEmail("jean@gmail.com");
        newUser.setPassword("password");
        newUser.setPasswordConfirmation("password");

        BindingResult result = new MapBindingResult(new HashMap<>(), "user");
        userService.userVerification(newUser, result);

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getFieldError("email")).isNotNull();
    }


    @Test
    @DisplayName("Vérification data - échec (mots de passe ne correspondent pas)")
    public void testUserVerificationFailurePasswordMismatch() {
        User newUser = new User();
        newUser.setUsername("NewUser");
        newUser.setEmail("newemail@gmail.com");
        newUser.setPassword("password");
        newUser.setPasswordConfirmation("differentpassword");

        BindingResult result = new MapBindingResult(new HashMap<>(), "user");
        userService.userVerification(newUser, result);

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getFieldError("password")).isNotNull();
        assertThat(result.getFieldError("passwordConfirmation")).isNotNull();
    }


    @Test
    @DisplayName("Recherche utilisateur par email - succès")
    public void testGetUserByEmailSuccess() {

        Optional<User> user = userService.getUserByEmail("jean@gmail.com");

        assertThat(user).isPresent();
        assertThat(user.get().getId()).isEqualTo(1);
        assertThat(user.get().getEmail()).isEqualTo("jean@gmail.com");
        assertThat(user.get().getUsername()).isEqualTo("Jean");
    }
}
