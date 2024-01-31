package com.perso.ecomm.playLoad.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class changePasswordRequest {

    @Size(min = 5, message = "should be more than 5 character")
    private String oldPassword;

    @Size(min = 5, message = "should be more than 5 character")
    private String newPassword;
}
