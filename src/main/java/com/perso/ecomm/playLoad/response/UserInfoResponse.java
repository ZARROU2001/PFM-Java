package com.perso.ecomm.playLoad.response;

import com.perso.ecomm.CustomUser.CustomUserDetails;
import com.perso.ecomm.user.User;
import com.perso.ecomm.user.UserDTO;

public record UserInfoResponse(
        String token,
        UserDTO userDTO) {
}
