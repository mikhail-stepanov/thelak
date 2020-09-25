package com.thelak.route.payments.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.models.CertificateModel;
import com.thelak.route.payments.models.IssuedCertificateModel;

import java.util.List;

public interface ICertificateService {

    String CERTIFICATE_GET = "/v1/certificate/get";
    String CERTIFICATE_LIST = "/v1/certificate/list";
    String CERTIFICATE_GENERATE = "/v1/certificate/generate";
    String CERTIFICATE_ACTIVATE = "/v1/certificate/activate";

    CertificateModel get(Long id) throws MicroServiceException;

    List<CertificateModel> list() throws MicroServiceException;

    IssuedCertificateModel generate(Long certificateId) throws MicroServiceException;

    Boolean activate(String uuid) throws MicroServiceException;
}
