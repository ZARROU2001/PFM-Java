package com.perso.ecomm.auth;

import com.perso.ecomm.CustomUser.CustomUserDetails;
import com.perso.ecomm.JWT.JWTUtil;
import com.perso.ecomm.playLoad.request.LoginRequest;
import com.perso.ecomm.playLoad.response.UserInfoResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public AuthenticationService(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    public UserInfoResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtUtil.issueToken(principal.getUsername(),principal.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));
        return new UserInfoResponse(token,principal);
    }
}
