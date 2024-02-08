package com.perso.ecomm.auth;

import com.perso.ecomm.playLoad.request.LoginRequest;
import com.perso.ecomm.playLoad.response.UserInfoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid LoginRequest loginRequest){
        UserInfoResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION,response.token())
                .body(response);
    }

}
