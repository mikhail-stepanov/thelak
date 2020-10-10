package com.thelak.route.payments.models.cloudpayments.cryptogramm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptogrammPayResponse {

    CryptogrammModel Model;

    Boolean Success;

    String Message;
}
