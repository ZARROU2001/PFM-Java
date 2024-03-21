package com.perso.ecomm.user;

import com.perso.ecomm.CustomUser.CustomUserDetails;
import com.perso.ecomm.JWT.JWTUtil;
import com.perso.ecomm.exception.DuplicateResourceException;
import com.perso.ecomm.exception.ResourceNotFoundException;
import com.perso.ecomm.playLoad.request.LoginRequest;
import com.perso.ecomm.playLoad.request.SignupRequest;
import com.perso.ecomm.playLoad.request.UserUpdateRequest;
import com.perso.ecomm.playLoad.request.changePasswordRequest;
import com.perso.ecomm.playLoad.response.UserInfoResponse;
import com.perso.ecomm.role.ERole;
import com.perso.ecomm.role.Role;
import com.perso.ecomm.role.RoleRepository;
import com.perso.ecomm.util.FileUploadUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class UserService {


    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    final String FOLDER_PATH = "src/main/resources/static/images";


    public UserService(AuthenticationManager authenticationManager, JWTUtil jwtUtil, PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("There's no user with id:" + id));
    }


    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("There's no user with id:" + userId)
        );
        userRepository.delete(user);
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(" User with id " + userId + " doesn't exist "));
        user.setUsername(userUpdateRequest.getUsername());
        user.setEmail(userUpdateRequest.getEmail());
        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());
        if(userUpdateRequest.getImageUrl() != null){
            FileUploadUtil.saveFile(FOLDER_PATH, userUpdateRequest.getImageUrl().getOriginalFilename(), userUpdateRequest.getImageUrl());
            user.setImageUrl("http://localhost:8080/images/" + userUpdateRequest.getImageUrl().getOriginalFilename());
        }

        return user;
    }

    @Transactional
    public void changePassword(Long userId, changePasswordRequest passwordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        " User with id " + userId + " doesn't exist "));
        user.setPassword(passwordRequest.getNewPassword());
    }

    public UserInfoResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmailOrUsername(principal.getUsername(), principal.getUsername());
        String token = jwtUtil.issueToken(principal.getUsername(), List.of(principal.getAuthorities().toString()));


        return new UserInfoResponse(token, user);
    }

    public User registerNewUser(SignupRequest signupRequest) throws IOException {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new DuplicateResourceException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new DuplicateResourceException("Error: email is already taken!");
        }
        String a = "";
        if (signupRequest.getImageUrl() != null) {
            FileUploadUtil.saveFile(FOLDER_PATH, signupRequest.getImageUrl().getOriginalFilename(), signupRequest.getImageUrl());
            a = signupRequest.getImageUrl().getOriginalFilename();
        }


        User user = new User(
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getFirstName(),
                signupRequest.getLastName(),
                signupRequest.getUsername(),
                "http://localhost:8080/images/users/default-image.png" + a);


        String strRoles = signupRequest.getRole();
        Role roles;

        if (strRoles == null) {
            roles = roleRepository.findRoleByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Role User not found."));
        } else if (strRoles.equals("admin")) {
            roles = roleRepository.findRoleByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Role admin not found."));
        } else if (strRoles.equals("moderator")) {
            roles = roleRepository.findRoleByName(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Role moderator not found."));
        } else {
            roles = roleRepository.findRoleByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Role User not found."));
        }

        user.setRole(roles);
        userRepository.save(user);
        return user;
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
                () -> new ResourceNotFoundException("user with id : " + userId + " not found")
        );

        Role targetRole = switch (role.toLowerCase()) {
            case "admin" -> roleRepository.findRoleByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("admin role not found"));
            case "moderator" -> roleRepository.findRoleByName(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new ResourceNotFoundException("moderator role not found"));
            case "user" -> roleRepository.findRoleByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("user role not found"));
            default -> throw new ResourceNotFoundException("Invalid role: " + role);
        };

        user.setRole(targetRole);
    }

    public void changePhoto(Long userId, MultipartFile multipartFile) throws IOException {

        User user = userRepository.findById(userId).orElseThrow(
        );

        String a = "";
        if (multipartFile != null) {
            FileUploadUtil.saveFile(FOLDER_PATH, multipartFile.getOriginalFilename(), multipartFile);
            a = multipartFile.getOriginalFilename();
        }

        user.setImageUrl("http://localhost:8080/images/" + a);

    }
}
