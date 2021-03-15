package com.thelak.route.payments.models.promo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeCreateRequest {

    Boolean active;

    String code;

    Integer months;

    String description;

    List<String> emails;

    Integer percent;
}
