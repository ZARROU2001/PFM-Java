package com.perso.ecomm.config;

import com.perso.ecomm.role.ERole;
import com.perso.ecomm.role.Role;
import com.perso.ecomm.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DatabaseInitializer(RoleRepository roleRepository1) {
        this.roleRepository = roleRepository1;
    }


    @Override
    public void run(String... args) throws Exception {

        Role userRole = new Role(ERole.ROLE_USER);
        Role adminRole = new Role(ERole.ROLE_ADMIN);
        Role modRole = new Role(ERole.ROLE_MODERATOR);

        roleRepository.save(userRole);
        roleRepository.save(adminRole);
        roleRepository.save(modRole);

    }
}