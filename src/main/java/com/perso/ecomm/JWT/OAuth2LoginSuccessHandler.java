package com.perso.ecomm.JWT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perso.ecomm.CustomUser.CustomUserDetails;
import com.perso.ecomm.playLoad.response.UserInfoResponse;
import com.perso.ecomm.user.User;
import com.perso.ecomm.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        // Find user by email
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"message\": \"User not found\"}");
            return;
        }
        User user = optionalUser.get();


        // Generate JWT token
        String token = jwtUtil.issueToken(user.getEmail(), List.of(user.getRole().getName().toString()));


        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(user);

        // Redirect to Angular app with the token in the URL
        response.sendRedirect("http://localhost:4200/login/success?token=" + token + "&user=" + json);





    }
}
