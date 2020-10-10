package com.thelak.route.payments.models.cloudpayments.cancel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelResponse {
    Boolean Success;

    String Message;
}
