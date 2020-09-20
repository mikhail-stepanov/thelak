package com.thelak.route.payments.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.models.CertificateModel;

import java.util.List;

public interface ICertificateService {

    String CERTIFICATE_GET = "/v1/certificate/get";
    String CERTIFICATE_LIST = "/v1/certificate/list";

    CertificateModel get(Long id) throws MicroServiceException;

    List<CertificateModel> list() throws MicroServiceException;
}
