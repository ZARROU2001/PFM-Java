package com.perso.ecomm.user;


import com.perso.ecomm.exception.RequestValidationException;
import com.perso.ecomm.playLoad.request.LoginRequest;
import com.perso.ecomm.playLoad.request.SignupRequest;
import com.perso.ecomm.playLoad.request.UserUpdateRequest;
import com.perso.ecomm.playLoad.request.changePasswordRequest;
import com.perso.ecomm.playLoad.response.UserInfoResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/paginate")
    public Page<User> paginateUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, direction, sortField);
        return userService.getSortedAndPagedData(pageable);
    }

    @DeleteMapping(path = "{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User with id : " + userId + "has deleted");

    }

    @PutMapping(path = "change_role/{userId}")
    public ResponseEntity<?> changeRoleOfUser(@PathVariable Long userId, String role) {
        userService.changeRole(userId, role);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("the role of user with id : " + userId + " changed successfully");
    }

    @PutMapping(path = "update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId, @Valid UserUpdateRequest userUpdateRequest, BindingResult result) throws IOException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        User user = userService.updateUser(userId, userUpdateRequest);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping(path = "password/{userId}")
    public ResponseEntity<?> changePassword(
            @PathVariable("userId") Long userId,
            changePasswordRequest passwordRequest,
            BindingResult result) {

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        userService.changePassword(userId, passwordRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("the password has changed successfully ");
    }

    @PutMapping(path = "changeImage/{userId}")
    public ResponseEntity<?> changePhoto(
            @PathVariable Long userId,
            MultipartFile multipartFile
    ) throws IOException {
        userService.changePhoto(userId,multipartFile);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Image updated successfully");
    }

    //Auth request

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid LoginRequest loginRequest, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        UserInfoResponse response = userService.login(loginRequest);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, response.token()).body(response);
    }

    @PostMapping(path = "signup")
    public ResponseEntity<?> addNewUser(@Valid SignupRequest signupRequest, BindingResult result) throws IOException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            throw new RequestValidationException(errors.toString());
        }
        User user = userService.registerNewUser(signupRequest);
        return ResponseEntity.ok()
                .body(user);
    }

    @PostMapping(path = "/signout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, userService.logoutUser())
                .body("You've been signed out!");
    }


}
