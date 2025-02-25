package com.perso.ecomm.user;

import com.perso.ecomm.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers() {
    }

    @Test
    void testGetUserById_ReturnsUserDTO() {
        // Mock data: creating a User entity
        User user = new User( "john@example.com", "","","","John","");

        // Mock repository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Call the method being tested
        UserDTO result = userService.getUserById(1L);

        // Validate the result
        assertNotNull(result);
        assertEquals("John", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void testGetUserById_ThrowsResourceNotFoundException() {
        // Mock repository behavior: no user found
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the method and expect an exception
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });

        // Validate the exception message
        assertEquals("There's no user with id: 1", exception.getMessage());
    }

    @Test
    void deleteUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void login() {
    }

    @Test
    void registerNewUser() {
    }

    @Test
    void logoutUser() {
    }

    @Test
    void getSortedAndPagedData() {
    }

    @Test
    void changeRole() {
    }

    @Test
    void changePhoto() {
    }

    @Test
    void getUserForOrder() {
    }

    @Test
    void handleForgotPassword() {
    }

    @Test
    void resetPassword() {
    }

    @Test
    void convertToDTO() {
    }
}