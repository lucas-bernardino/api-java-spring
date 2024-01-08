package com.example.demo.models;


import jakarta.validation.constraints.NotNull;

public record RegisterRecord(@NotNull(groups = User.CreateUser.class) String username,
                             @NotNull(groups = User.CreateUser.class) String password,
                             @NotNull(groups = User.CreateUser.class) UserEnum role
) {
}
