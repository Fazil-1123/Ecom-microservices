package com.ecommerce.user.mappers;

import com.ecommerce.user.dtos.UsersDto;
import com.ecommerce.user.models.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    Users toEntity(UsersDto dto);

    UsersDto toDto(Users user);
}
