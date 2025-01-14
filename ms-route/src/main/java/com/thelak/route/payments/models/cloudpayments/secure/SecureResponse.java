package com.thelak.route.payments.models.cloudpayments.secure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecureResponse {

    String Message;

    Boolean Success;

    SecureModel Model;
}
