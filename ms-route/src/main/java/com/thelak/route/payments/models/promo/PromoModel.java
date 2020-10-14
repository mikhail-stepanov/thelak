package com.thelak.route.payments.models.promo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoModel {

    Boolean success;

    Integer months;

    Integer days;

    String description;
}
