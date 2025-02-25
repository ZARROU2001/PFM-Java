//package com.perso.ecomm.oauth;
//
//import com.perso.ecomm.user.User;
//import lombok.Getter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Map;
//
//public class CustomOAuth2User implements OAuth2User, UserDetails {
//
//    private final User user;
//    private final Map<String, Object> attributes;
//    @Getter
//    private final String jwtToken;
//
//    public CustomOAuth2User(User user, Map<String, Object> attributes, String jwtToken) {
//        this.user = user;
//        this.attributes = attributes;
//        this.jwtToken = jwtToken;
//    }
//
//    @Override
//    public Map<String, Object> getAttributes() {
//        return attributes;
//    }
//
//    @Override
//    public String getName() {
//        return user.getUsername();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.EMPTY_SET;
//    }
//
//    @Override
//    public String getPassword() {
//        return user.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return user.getUsername();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//}
