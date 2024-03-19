package com.perso.ecomm.playLoad.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserUpdateRequest {

    @Size(min = 3, max = 20,message = "firstName should be more than 3")
    private String firstName;

    @Size(min = 3, max = 20,message = "lastName should be more than 3")
    private String lastName;

    @Size(min = 3, max = 20,message = "username should be more than 3")
    private String username;

    @Email(message = "email incorrect")
    private String email;

    private MultipartFile imageUrl;
}
