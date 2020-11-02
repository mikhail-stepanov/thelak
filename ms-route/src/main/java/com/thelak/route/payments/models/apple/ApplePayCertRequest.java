package com.thelak.route.payments.models.apple;

import com.thelak.route.payments.models.certificate.CertificateViewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplePayCertRequest {

    String ValidationUrl;

    Long certificateId;

    String fio;

    String description;

    CertificateViewType type;
}
