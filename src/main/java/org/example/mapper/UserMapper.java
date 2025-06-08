package org.example.mapper;

import org.example.model.UserEntity;
import org.example.model.dto.NewUserDto;
import org.example.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserEntity toUserEntity(NewUserDto newUserDto);

    UserDto toUserDto(UserEntity userEntity);

    NewUserDto toNewUserDto(UserEntity userEntity);
}
