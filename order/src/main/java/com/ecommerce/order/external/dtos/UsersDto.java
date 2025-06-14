package com.ecommerce.order.external.dtos;

import lombok.Data;

@Data
public class UsersDto {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private AddressDto address;
}
