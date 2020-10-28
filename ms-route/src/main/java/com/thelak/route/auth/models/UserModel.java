package com.thelak.route.auth.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    Long id;

    String email;

    String phone;

    String name;

    String city;

    String country;

    LocalDate birthday;

    Boolean isSubscribe;

    Boolean isAdmin;

    LocalDateTime subscriptionDate;

    LocalDateTime createdDate;

    LocalDateTime modifiedDate;

    LocalDateTime deletedDate;

}
