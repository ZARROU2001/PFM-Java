package com.perso.ecomm.user;

import com.perso.ecomm.playLoad.request.LoginRequest;
import com.perso.ecomm.playLoad.request.SignupRequest;
import com.perso.ecomm.playLoad.request.UserUpdateRequest;
import com.perso.ecomm.playLoad.request.changePasswordRequest;
import com.perso.ecomm.role.ERole;
import com.perso.ecomm.role.Role;
import com.perso.ecomm.role.RoleRepository;
import com.perso.ecomm.util.FileUploadUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class UserService {


    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    final String FOLDER_PATH = "src/main/resources/static/images/users";


    public UserService(UserRepository userRepository, RoleRepository roleRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("There's no user with id:" + id ));
    }


    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("There's no user with id:" + userId )
        );
        userRepository.delete(user);
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(" User with id " + userId + " doesn't exist "));
            user.setUsername(userUpdateRequest.getUsername());
            user.setEmail(userUpdateRequest.getEmail());
            user.setFullName(userUpdateRequest.getFullName());
            FileUploadUtil.saveFile(FOLDER_PATH, userUpdateRequest.getImageUrl().getOriginalFilename(), userUpdateRequest.getImageUrl());
            user.setImageUrl("http://localhost:8080/images/users/" + userUpdateRequest.getImageUrl().getOriginalFilename());
            return user;
    }

    @Transactional
    public void changePassword(Long userId, changePasswordRequest passwordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        " User with id " + userId + " doesn't exist "));
        user.setPassword(passwordRequest.getNewPassword());
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        User user = userRepository.findByEmailOrUsername(loginRequest.getUsername(), loginRequest.getUsername());
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<?> registerNewUser(SignupRequest signupRequest) throws IOException {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        FileUploadUtil.saveFile(FOLDER_PATH, signupRequest.getImageUrl().getOriginalFilename(), signupRequest.getImageUrl());


        User user = new User(
                signupRequest.getEmail(),
                "",
                signupRequest.getFullName(),
                signupRequest.getUsername(),
                "http://localhost:8080/images/users/" + signupRequest.getImageUrl().getOriginalFilename());

        String strRoles = signupRequest.getRole();
        Role roles;

        if (strRoles == null) {
            roles = roleRepository.findRoleByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new EntityNotFoundException("Error: Role User not found."));
        } else if (strRoles.equals("admin")) {
            roles = roleRepository.findRoleByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new EntityNotFoundException("Error: Role is not found."));
        } else if (strRoles.equals("moderator")) {
            roles = roleRepository.findRoleByName(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new EntityNotFoundException("Error: Role is not found."));
        } else {
            roles = roleRepository.findRoleByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new EntityNotFoundException("Error: Role User not found."));
        }

        user.setRole(roles);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    public String logoutUser() {
        return "logged out";
    }

    public Page<User> getSortedAndPagedData(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public void changeRole(Long userId, String role) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("user with id : " + userId + " not found")
        );

        Role targetRole = switch (role.toLowerCase()) {
            case "admin" -> roleRepository.findRoleByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new EntityNotFoundException("admin role not found"));
            case "moderator" -> roleRepository.findRoleByName(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new EntityNotFoundException("moderator role not found"));
            case "user" -> roleRepository.findRoleByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new EntityNotFoundException("user role not found"));
            default -> throw new EntityNotFoundException("Invalid role: " + role);
        };

        user.setRole(targetRole);
    }
}
