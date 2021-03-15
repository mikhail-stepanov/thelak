package com.thelak.route.payments.models.certificate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailCertificateRequest {

    String description;

    CertificateViewType type;

    Long id;

    List<String> email;

    List<String> fio;
}