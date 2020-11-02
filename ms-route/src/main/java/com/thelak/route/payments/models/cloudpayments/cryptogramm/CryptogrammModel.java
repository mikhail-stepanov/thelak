package com.thelak.route.payments.models.cloudpayments.cryptogramm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptogrammModel {

    Long TransactionId;

    String PaReq;

    String AcsUrl;

    Boolean IFrameIsAllowed;

    String FrameWidth;

    String FrameHeight;

    String ThreeDsCallbackId;

    String Token;
}
