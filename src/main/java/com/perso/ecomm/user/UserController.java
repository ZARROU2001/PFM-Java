package com.perso.ecomm.user;

import com.perso.ecomm.exception.RequestValidationException;
import com.perso.ecomm.playLoad.request.*;
import com.perso.ecomm.playLoad.response.UserInfoResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("{Id}")
    public UserDTO getUser( @PathVariable Long Id) {
        return userService.getUserById(Id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.getUsers();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/paginate")
    public Page<UserDTO> paginateUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, direction, sortField);
        return userService.getSortedAndPagedData(pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User with id: " + userId + " has been deleted");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(path = "change_role/{userId}")
    public ResponseEntity<?> changeRoleOfUser(
            @PathVariable Long userId,
            @RequestParam String role) {
        userService.changeRole(userId, role);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("The role of user with id: " + userId + " changed successfully");
    }

    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    @PutMapping(path = "update/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable("userId") Long userId,
            @Valid UserUpdateRequest userUpdateRequest,
            BindingResult result) throws IOException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        UserDTO userDTO = userService.updateUser(userId, userUpdateRequest);
        return ResponseEntity.ok().body(userDTO);
    }

    @PutMapping(path = "changeImage/{userId}")
    public ResponseEntity<?> changePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile multipartFile) throws IOException {
        userService.changePhoto(userId, multipartFile);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Image updated successfully");
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.handleForgotPassword(request.getEmail());
        return ResponseEntity.ok("If the email exists, a reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password has been reset successfully.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }


    @PostMapping("login")
    public ResponseEntity<?> login(@Valid LoginRequest loginRequest, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        UserInfoResponse response = userService.login(loginRequest);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + response.token()).body(response);
    }

    @PostMapping(path = "signup")
    public ResponseEntity<?> addNewUser(SignupRequest signupRequest, BindingResult result) throws IOException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            throw new RequestValidationException(errors.toString());
        }
        UserDTO userDTO = userService.registerNewUser(signupRequest);
        return ResponseEntity.ok().body(userDTO);
    }

    @PostMapping(path = "/signout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, userService.logoutUser())
                .body("You've been signed out!");
    }


    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long id) {
        try {
            byte[] imageBytes = userService.getUserImage(id);
            if (imageBytes == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
