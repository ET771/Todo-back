package org.acme.infrastructure.mapper;

import org.acme.domain.models.User;
import org.acme.infrastructure.entities.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity entity){
        User user= new User();
        user.setId(entity.getId());
        user.setActive(entity.isActive());
        user.setEmail(entity.getEmail());
        user.setFirebaseUuid(entity.getFirebaseUuid());
        user.setRole(entity.getRole());
        user.setFullName(entity.getFullName());
        return user;
    }

    public static UserEntity toEntity(User user){
        UserEntity entity= new UserEntity();
        entity.setId(user.getId());
        entity.setActive(user.isActive());
        entity.setEmail(user.getEmail());
        entity.setFirebaseUuid(user.getFirebaseUuid());
        entity.setRole(user.getRole());
        entity.setFullName(user.getFullName());
        return entity;
    }
}