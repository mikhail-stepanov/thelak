package com.thelak.route.payments.models.apple;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplePayValidationResponse {

    ApplePayValidationModel Model;

    Boolean Success;

    String Message;
}
