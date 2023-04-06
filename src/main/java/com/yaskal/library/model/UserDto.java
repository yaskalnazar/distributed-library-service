package com.yaskal.library.model;

import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String city;
    private String address;
    private String password;

}

