package com.thelak.route.payments.models.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuySubscriptionRequest {

    Long subscriptionId;

    String cardCryptogramPacket;

    String cardName;

    Long promoId;

}
