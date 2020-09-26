package com.thelak.route.payments.models.cloudpayments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptogrammPayRequest {

    String Name;

    String Description;

    Integer Amount;

    String Currency;

    String CardCryptogramPacket;

    String AccountId;
}