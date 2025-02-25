package com.perso.ecomm.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    boolean existsRoleByName(ERole name);

    boolean existsRoleById(Integer id);

    Optional<Role> findRoleByName(ERole eRole);
}
