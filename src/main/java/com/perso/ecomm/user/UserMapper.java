package com.perso.ecomm.user;

import com.perso.ecomm.role.ERole;
import com.perso.ecomm.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Mapping User to UserDTO (Extract role name from Role entity)
    // Mapping User entity to UserDTO
    @Mapping(source = "role.name", target = "roleName")
    UserDTO userToUserDTO(User user);






}
