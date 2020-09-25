package com.thelak.route.payments.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateModel {

    Long id;

    Long months;

    String name;

    Integer price;

    String priceStr;

    String priceStr2;

    Integer cover;

    String description;

    String length;

    List<String> list;

    LocalDateTime createdDate;

    LocalDateTime modifiedDate;

    LocalDateTime deletedDate;

}
