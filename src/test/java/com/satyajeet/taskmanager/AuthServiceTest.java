package com.satyajeet.taskmanager;

import com.satyajeet.taskmanager.dto.Dtos.*;
import com.satyajeet.taskmanager.entity.User;
import com.satyajeet.taskmanager.repository.UserRepository;
import com.satyajeet.taskmanager.security.JwtUtil;
import com.satyajeet.taskmanager.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;
    @InjectMocks private AuthService authService;

    @Test
    void register_success() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtUtil.generateToken("test@test.com")).thenReturn("jwt-token");

        AuthResponse response = authService.register(new RegisterRequest(
                "Satyajeet", "test@test.com", "password",
                "What was your first pet's name?", "Tommy"));

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Satyajeet", response.getName());
    }

    @Test
    void register_duplicateEmail_throwsException() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
                authService.register(new RegisterRequest(
                        "Satyajeet", "test@test.com", "password",
                        "What was your first pet's name?", "Tommy")));
    }

    @Test
    void login_success() {
        User user = User.builder().id(1L).name("Satyajeet").email("test@test.com").password("encoded").build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("test@test.com")).thenReturn("jwt-token");
        doNothing().when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        AuthResponse response = authService.login(new LoginRequest("test@test.com", "password"));

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }
}