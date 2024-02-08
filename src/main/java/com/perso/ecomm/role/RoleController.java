package com.perso.ecomm.role;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @GetMapping
    public List<Role> getAuth(){
        return roleService.getAuths();
    }

    @PostMapping
    public ResponseEntity<?> postAuth(Role role){
            Role role1 = roleService.register(role);
            return ResponseEntity.ok(role1);

    }

    @DeleteMapping(path = "{roleId}")
    public String deleteAuth(
            @PathVariable Integer roleId ) {
        roleService.deleteRole(roleId);
        return "Role deleted";
    }


}
