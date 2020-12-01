package com.thelak.route.payments.models.certificate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCertificateRequest {

    Long months;

    String name;

    Integer price;

    String priceStr;

    String priceStr2;

    Integer cover;

    String description;

    String length;

    List<String> list;
}
