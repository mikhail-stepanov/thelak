package com.thelak.route.payments.models.certificate;

import com.thelak.route.payments.models.cloudpayments.secure.SecureResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyCertificateResponse {

    SecureResponse payResponse;

    IssuedCertificateModel certificate;

    String MD;

    Boolean success;
}
