package com.thelak.route.payments.models.apple;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplePaySubRequest {

    String cryptogram;

    Long subscriptionId;

    Long promoId;
}
