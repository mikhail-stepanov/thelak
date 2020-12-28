package com.thelak.route.payments.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsConfigModel {

    String publicId;

    String merchantId;

    String googleId;
}
