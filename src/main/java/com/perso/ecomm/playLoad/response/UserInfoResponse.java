package com.perso.ecomm.playLoad.response;

import com.perso.ecomm.CustomUser.CustomUserDetails;
import com.perso.ecomm.user.User;

public record UserInfoResponse(
        String token,
        CustomUserDetails user) {
}
