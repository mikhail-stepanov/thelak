package com.thelak.route.auth.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginRequest {

    @NotNull
    private String email;

    @NotNull
    private String password;

}