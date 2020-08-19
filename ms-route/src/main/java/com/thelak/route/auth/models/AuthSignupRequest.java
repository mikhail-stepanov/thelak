package com.thelak.route.auth.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthSignupRequest {

    @NotNull
    String email;

    @NotNull
    String password;

    @NotNull
    String phone;

    String name;

    String city;

    String country;

    LocalDate birthday;

}