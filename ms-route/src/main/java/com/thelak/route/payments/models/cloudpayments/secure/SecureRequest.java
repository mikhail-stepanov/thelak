package com.thelak.route.payments.models.cloudpayments.secure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecureRequest {

    Long TransactionId;

    String PaRes;
}
