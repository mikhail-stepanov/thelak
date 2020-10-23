package com.thelak.route.payments.models.certificate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyCertificateRequest {

    Long certificateId;

    String fio;

    String description;

    String type;

    String cardCryptogramPacket;

    String cardName;
}
