package com.perso.ecomm.user;

import com.perso.ecomm.CustomUser.CustomUserDetails;
import com.perso.ecomm.JWT.JWTUtil;
import com.perso.ecomm.config.EmailService;
import com.perso.ecomm.exception.DuplicateResourceException;
import com.perso.ecomm.exception.ResourceNotFoundException;
import com.perso.ecomm.playLoad.request.LoginRequest;
import com.perso.ecomm.playLoad.request.OrderRequest;
import com.perso.ecomm.playLoad.request.SignupRequest;
import com.perso.ecomm.playLoad.request.UserUpdateRequest;
import com.perso.ecomm.playLoad.response.UserInfoResponse;
import com.perso.ecomm.role.ERole;
import com.perso.ecomm.role.Role;
import com.perso.ecomm.role.RoleRepository;
import com.perso.ecomm.util.ImageStorageService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {


    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final ImageStorageService imageStorageService;

    final String FOLDER_PATH = "src/main/resources/static/images";

    public UserService(AuthenticationManager authenticationManager, JWTUtil jwtUtil, PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, EmailService emailService, ImageStorageService imageStorageService) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.imageStorageService = imageStorageService;
    }

    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("There's no user with id: " + id));
        return convertToDTO(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("There's no user with id: " + userId));
        userRepository.delete(user);
    }

    @Transactional
    public UserDTO updateUser(Long userId, UserUpdateRequest userUpdateRequest) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " doesn't exist"));

        // Validate unique username and email
        if (!user.getUsername().equals(userUpdateRequest.getUsername()) &&
                userRepository.existsByUsername(userUpdateRequest.getUsername())) {
            throw new DuplicateResourceException("Error: Username is already taken!");
        }
        if (!user.getEmail().equals(userUpdateRequest.getEmail()) &&
                userRepository.existsByEmail(userUpdateRequest.getEmail())) {
            throw new DuplicateResourceException("Error: Email is already taken!");
        }

        // Update user details
        user.setUsername(userUpdateRequest.getUsername());
        user.setEmail(userUpdateRequest.getEmail());
        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());

        // Handle image update if provided
        if (userUpdateRequest.getImageUrl() != null && !userUpdateRequest.getImageUrl().isEmpty()) {
            String imageUrl = imageStorageService.saveImage(user.getUsername(), userUpdateRequest.getImageUrl());
            user.setImageUrl(imageUrl);
        }

        userRepository.save(user);
        return convertToDTO(user);
    }

    @Transactional
    public void changePhoto(Long userId, MultipartFile multipartFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        if (multipartFile != null && !multipartFile.isEmpty()) {
            String imageUrl = imageStorageService.saveImage(user.getUsername(), multipartFile);
            user.setImageUrl(imageUrl);
            userRepository.save(user);
        }
    }

    public UserInfoResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmailOrUsername(principal.getUsername(), principal.getUsername());
        String token = jwtUtil.issueToken(principal.getUsername(), List.of(principal.getAuthorities().toString()));

        return new UserInfoResponse(token, convertToDTO(user));
    }

    public UserDTO registerNewUser(SignupRequest signupRequest) throws IOException {
        // Check if username or email already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new DuplicateResourceException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new DuplicateResourceException("Error: Email is already taken!");
        }

        // Handle image upload
        String imageUrl = null;
        MultipartFile imageFile = signupRequest.getImageUrl();

        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = imageStorageService.saveImage(signupRequest.getUsername(), imageFile);

        }

        // Create new User entity
        User user = new User(
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getFirstName(),
                signupRequest.getLastName(),
                signupRequest.getUsername(),
                imageUrl
        );

        // Determine role (default to "ROLE_USER")
        String roleName = Optional.ofNullable(signupRequest.getRole())
                .filter(role -> !role.isEmpty())
                .map(String::toUpperCase)
                .orElse("USER"); // Default to USER

        Role role = roleRepository.findRoleByName(ERole.valueOf("ROLE_" + roleName))
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role not found."));

        user.setRole(role);

        // Save the user
        userRepository.save(user);

        return convertToDTO(user);
    }

    public String uploadUserImage(String userId, MultipartFile file) throws IOException {
        Optional<User> userOptional = userRepository.findByUsername(userId);
        if (userOptional.isEmpty()) {
            return "User not found";
        }

        User user = userOptional.get();

        // Delete old image if it exists
        if (user.getImageUrl() != null) {
            imageStorageService.deleteImage(user.getImageUrl());
        }

        // Store the new image
        String filePath = imageStorageService.saveImage(userId, file);
        user.setImageUrl(filePath);
        userRepository.save(user);

        return "Image uploaded successfully!";
    }

    public byte[] getUserImage(Long userId) throws IOException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty() || userOptional.get().getImageUrl() == null) {
            return null;
        }

        return imageStorageService.loadImage(userOptional.get().getImageUrl());
    }


    public String logoutUser() {
        // Logout logic depends on the implementation
        return "logged out";
    }

    public Page<UserDTO> getSortedAndPagedData(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Transactional
    public void changeRole(Long userId, String role) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id : " + userId + " not found"));

        Role targetRole = roleRepository.findRoleByName(ERole.valueOf("ROLE_" + role.toUpperCase())).orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.setRole(targetRole);
        userRepository.save(user);
    }

    public User getUserForOrder(OrderRequest orderRequest) {
        if (orderRequest.getUserId() != null) {
            // Retrieve authenticated user by ID
            return userRepository.findById(orderRequest.getUserId()).orElseThrow(() -> new ResourceNotFoundException("There's no user with id: " + orderRequest.getUserId()));
        } else {
            // Create a guest user with minimal information
            return createGuestUser(orderRequest.getGuestName(), orderRequest.getGuestEmail());
        }
    }

    private User createGuestUser(String guestName, String guestEmail) {
        // Validate guest information
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest name cannot be empty");
        }

        User guestUser = new User();
        guestUser.setUsername(guestName);
        guestUser.setEmail(guestEmail != null ? guestEmail : "");  // Email can be optional for guests
        Role guestRole = roleRepository.findRoleByName(ERole.ROLE_GUEST).orElseThrow(() -> new ResourceNotFoundException("Error: Guest Role is not found."));
        guestUser.setRole(guestRole);

        return userRepository.save(guestUser);
    }


    // Method to handle forgot password flow
    public void handleForgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Generate password reset token
            String token = jwtUtil.generatePasswordResetToken(user.getEmail());

            // Construct reset link and send email
            String resetLink = "http://localhost:4200/authentication/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        }
        // Always respond with a success message to prevent exposing whether the email exists
    }

    // Method to handle password reset
    public void resetPassword(String token, String newPassword) {
        if (!jwtUtil.validatePasswordResetToken(token)) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        String email = jwtUtil.getSubject(token); // Extract email from token
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Update the user's password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);  // Save the updated user with the new password
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }


    public UserDTO convertToDTO(User user) {
        return userMapper.userToUserDTO(user);
    }


}
