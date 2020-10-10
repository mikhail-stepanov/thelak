package com.thelak.route.payments.models.certificate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssuedCertificateModel {

    Long id;

    String uuid;

    String fio;

    Boolean active;

    LocalDateTime activeDate;

    CertificateModel certificateModel;
}
