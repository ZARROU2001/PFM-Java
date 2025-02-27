package com.perso.ecomm.role;

import com.perso.ecomm.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role register(Role role) {
        boolean IsExist = roleRepository.existsRoleByName(role.getName());
        if(IsExist){
            throw new ResourceNotFoundException("this role name already exist : " + role.getName());
        }
        roleRepository.save(role);
        return role;
    }
    public void deleteRole(Integer roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(
                () -> new ResourceNotFoundException("There's no role with id : " + roleId)
        );
        roleRepository.delete(role);
    }
    public List<Role> getAuths() {
        return roleRepository.findAll();

    }
}
