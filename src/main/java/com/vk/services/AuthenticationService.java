package com.vk.services;

import com.vk.dto.LoginRequest;
import com.vk.dto.LoginResponse;
import com.vk.dto.RegisterRequest;
import com.vk.dto.RegisterResponse;
import com.vk.entities.User;
import com.vk.enums.PasswordCheckStatus;
import com.vk.exceptions.EmailAlreadyRegisteredException;
import com.vk.exceptions.UserNotFoundException;
import com.vk.exceptions.WeakPasswordException;
import com.vk.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                 JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        if (registerRequest == null || registerRequest.getEmail() == null || registerRequest.getPassword() == null) {
            throw new IllegalArgumentException("email and password must be provided");
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyRegisteredException("user with email [" + registerRequest.getEmail() + "] already registered");
        }

        PasswordCheckStatus passwordCheckStatus = checkPasswordStrength(registerRequest.getPassword());
        if (passwordCheckStatus == PasswordCheckStatus.VERY_WEAK || passwordCheckStatus == PasswordCheckStatus.WEAK) {
            throw new WeakPasswordException("password status is [" + passwordCheckStatus + "]. your password need to be at least medium strength");
        }

        User user = new User()
                .setEmail(registerRequest.getEmail())
                .setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);

        return new RegisterResponse().setUserId(user.getId()).setPasswordCheckStatus(passwordCheckStatus.getStatus());
    }

    public LoginResponse login(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            throw new IllegalArgumentException("email and password must be provided");
        }

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("user with email [" + loginRequest.getEmail() + "] not found"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        String token = jwtService.generateToken(user);

        return new LoginResponse().setAccessToken(token);
    }

    private PasswordCheckStatus checkPasswordStrength(String password) {
        if (password.length() < 8) {
            return PasswordCheckStatus.VERY_WEAK;
        }

        boolean hasLowerLetters = false;
        boolean hasUpperLetters = false;
        boolean hasDigits = false;
        boolean hasSpecialCharacters = false;
        for (char ch : password.toCharArray()) {
            if (Character.isLetter(ch)) {
                if (Character.isLowerCase(ch)) {
                    hasLowerLetters = true;
                } else {
                    hasUpperLetters = true;
                }
            } else if (Character.isDigit(ch)) {
                hasDigits = true;
            } else {
                hasSpecialCharacters = true;
            }
        }

        if (hasLowerLetters && hasUpperLetters && hasDigits && hasSpecialCharacters && password.length() >= 16) {
            return PasswordCheckStatus.PERFECT;
        } else if (hasLowerLetters && hasUpperLetters && hasDigits && hasSpecialCharacters) {
            return PasswordCheckStatus.GOOD;
        } else if ((hasLowerLetters || hasUpperLetters) && hasDigits) {
            return PasswordCheckStatus.MEDIUM;
        } else  {
            return PasswordCheckStatus.WEAK;
        }
    }
}
