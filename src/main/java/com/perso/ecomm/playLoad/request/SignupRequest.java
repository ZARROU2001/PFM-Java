package com.perso.ecomm.playLoad.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SignupRequest {

    @NotNull
    @Size(min = 3, max = 20, message = "firstName should be more than 3")
    private String firstName;

    @NotNull
    @Size(min = 3, max = 20, message = "lastName should be more than 3")
    private String lastName;

    @Size(min = 3, max = 20, message = "username should be more than 3")
    private String username;

    @Email(message = "email incorrect")
    private String email;

    private String role;

    private MultipartFile imageUrl;

    @Size(min = 6, max = 40, message = "Password should not be empty and more than 6 character")
    private String password;

}
