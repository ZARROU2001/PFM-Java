package com.perso.ecomm.user;


import com.perso.ecomm.playLoad.request.LoginRequest;
import com.perso.ecomm.playLoad.request.SignupRequest;
import com.perso.ecomm.playLoad.request.UserUpdateRequest;
import com.perso.ecomm.playLoad.request.changePasswordRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;




    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
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
        try{
            userService.deleteUser(userId);
            return ResponseEntity.ok("User with id : " + userId + "has deleted");
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @PutMapping(path = "change_role/{userId}")
    public ResponseEntity<?> changeRoleOfUser(@PathVariable Long userId, String role) {
        try {
            userService.changeRole(userId, role);
            return ResponseEntity.ok("the role of user with id : " + userId + " changed successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping(path = "update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") Long userId, @Valid UserUpdateRequest userUpdateRequest,BindingResult result ) throws IOException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        try{
            User user = userService.updateUser(userId, userUpdateRequest);
            return ResponseEntity.ok().body(user);
        }catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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
        try {
            userService.changePassword(userId,passwordRequest);
            return ResponseEntity.ok("the password has changed successfully ");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }/*catch (UsernameNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }*/
    }

    //Auth request

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid  LoginRequest loginRequest, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        return userService.login(loginRequest);
    }

    @PostMapping(path = "signup")
    public ResponseEntity<?> addNewUser(@Valid SignupRequest signupRequest, BindingResult result) throws IOException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        try {
            return userService.registerNewUser(signupRequest);
        } catch ( EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @PostMapping(path = "/signout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, userService.logoutUser())
                .body("You've been signed out!");
    }


}
