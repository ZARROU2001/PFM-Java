package com.perso.ecomm.user;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String imageUrl;
    private String roleName;  // Only role name, not the entire Role entity

}
