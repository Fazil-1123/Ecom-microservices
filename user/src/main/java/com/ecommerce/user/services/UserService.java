package com.ecommerce.user.services;

import com.ecommerce.user.dtos.UsersDto;

import java.util.List;

public interface UserService {

    List<UsersDto> getUsers();

    UsersDto addUser(UsersDto user);

    UsersDto findById(Long id);

    UsersDto updateUser(Long id, UsersDto users);
}
