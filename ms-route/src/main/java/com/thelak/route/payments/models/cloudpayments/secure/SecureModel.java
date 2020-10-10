package com.thelak.route.payments.models.cloudpayments.secure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecureModel {

    String Token;

    String CardHolderMessage;

    String Status;

    String Description;

    Double Amount;

    String Reason;
}
