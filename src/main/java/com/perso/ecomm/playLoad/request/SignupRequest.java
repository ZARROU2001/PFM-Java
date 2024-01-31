package com.perso.ecomm.playLoad.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter
@Setter
public class SignupRequest {

    @NotNull
    @Size(min = 3, max = 20,message = "fullName should be more than 3")
    private String fullName;

    @NotNull
    @Size(min = 3, max = 20,message = "username should be more than 3")
    private String username;

    @Email(message = "email incorrect")
    private String email;

    private String role;

    private MultipartFile imageUrl;

    @Size(min = 6, max = 40,message = "Password should not be empty")
    private String password;

}
