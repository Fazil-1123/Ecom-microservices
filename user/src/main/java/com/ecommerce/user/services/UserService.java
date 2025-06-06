package com.ecommerce.user.services;

import com.ecommerce.user.dtos.UsersDto;

import java.util.List;

public interface UserService {

    List<UsersDto> getUsers();

    UsersDto addUser(UsersDto user);

    UsersDto findById(String id);

    UsersDto updateUser(String id, UsersDto users);
}
