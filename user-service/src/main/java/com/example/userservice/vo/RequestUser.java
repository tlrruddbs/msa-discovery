package com.example.userservice.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class RequestUser {
    @NotNull(message="Email cannot be null")
    @Size(min=2, message="Email not be less than two character")
    @Email
    private String email;
    @NotNull(message="name cannot be null")
    @Size(min=2, message="name not be less than two character")
    private String name;
    @NotNull(message="pwd cannot be null")
    @Size(min=8, message="pwd not be less than 8 character")
    private String pwd;

}
