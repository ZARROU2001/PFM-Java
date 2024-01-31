package com.perso.ecomm.playLoad.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserUpdateRequest {

    @NotNull
    @Size(min = 3, max = 20,message = "fullName should be more than 3")
    private String fullName;

    @NotNull
    @Size(min = 3, max = 20,message = "username should be more than 3")
    private String username;

    @Email(message = "email incorrect")
    private String email;

    private MultipartFile imageUrl;
}
