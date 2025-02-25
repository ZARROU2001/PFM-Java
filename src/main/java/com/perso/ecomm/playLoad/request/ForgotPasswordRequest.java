package com.perso.ecomm.playLoad.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ForgotPasswordRequest {
    @NotBlank
    @Email
    private String email;
}
