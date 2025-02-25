package com.perso.ecomm.config;

import com.perso.ecomm.orders.order.Order;
import com.perso.ecomm.orders.order.OrderRepository;
import com.perso.ecomm.orders.order.OrderStatus;
import com.perso.ecomm.orders.orderItem.OrderItem;
import com.perso.ecomm.orders.orderItem.OrderItemRepository;
import com.perso.ecomm.product.Product;
import com.perso.ecomm.product.ProductRepository;
import com.perso.ecomm.productCategory.ProductCategory;
import com.perso.ecomm.productCategory.ProductCategoryRepository;
import com.perso.ecomm.role.ERole;
import com.perso.ecomm.role.Role;
import com.perso.ecomm.role.RoleRepository;
import com.perso.ecomm.user.User;
import com.perso.ecomm.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DatabaseInitializer(RoleRepository roleRepository1) {
        this.roleRepository = roleRepository1;
    }


    @Override
    public void run(String... args) throws Exception {

        if (!roleRepository.existsRoleByName(ERole.ROLE_USER)) {
            Role userRole = new Role(ERole.ROLE_USER);
            roleRepository.save(userRole);
        }

        if (!roleRepository.existsRoleByName(ERole.ROLE_ADMIN)) {
            Role adminRole = new Role(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }

        if (!roleRepository.existsRoleByName(ERole.ROLE_MODERATOR)) {
            Role modRole = new Role(ERole.ROLE_MODERATOR);
            roleRepository.save(modRole);
        }

        if (!roleRepository.existsRoleByName(ERole.ROLE_GUEST)) {
            Role guest = new Role(ERole.ROLE_GUEST);
            roleRepository.save(guest);
        }




    }
}