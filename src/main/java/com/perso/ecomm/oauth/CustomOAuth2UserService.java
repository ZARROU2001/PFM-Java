package com.perso.ecomm.oauth;

import com.perso.ecomm.role.ERole;
import com.perso.ecomm.role.Role;
import com.perso.ecomm.role.RoleRepository;
import com.perso.ecomm.user.User;
import com.perso.ecomm.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Extract user details
        String email = oAuth2User.getAttribute("email");

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            // New user, create and assign ROLE_USER
            Role userRole = roleRepository.findRoleByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            User newuser = new User();
            newuser.setEmail(email);
            newuser.setFirstName(oAuth2User.getAttribute("given_name"));
            newuser.setLastName(oAuth2User.getAttribute("family_name"));
            newuser.setUsername(oAuth2User.getAttribute("email"));
            newuser.setImageUrl(oAuth2User.getAttribute("picture"));
            newuser.setPassword(passwordEncoder.encode("oauth2_password"));
            newuser.setRole(userRole);
            userRepository.save(newuser);
        }

        return oAuth2User;
    }
}
