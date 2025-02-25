package com.perso.ecomm.CustomUser;

import com.perso.ecomm.user.User;
import com.perso.ecomm.user.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable("userDetails") // Cache user details to avoid frequent DB hits.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrUsername(username, username);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
        return new CustomUserDetails(user);
    }
}
