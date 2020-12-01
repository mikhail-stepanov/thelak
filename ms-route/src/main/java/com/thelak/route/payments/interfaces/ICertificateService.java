package com.thelak.route.payments.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.models.certificate.CertificateModel;
import com.thelak.route.payments.models.certificate.CreateCertificateRequest;
import com.thelak.route.payments.models.certificate.IssuedCertificateModel;

import java.util.List;

public interface ICertificateService {

    String CERTIFICATE_GET = "/v1/certificate/get";
    String CERTIFICATE_CREATE = "/v1/certificate/create";
    String CERTIFICATE_UPDATE = "/v1/certificate/update";
    String CERTIFICATE_DELETE = "/v1/certificate/delete";
    String CERTIFICATE_LIST = "/v1/certificate/list";
    String CERTIFICATE_GENERATE = "/v1/certificate/generate";
    String CERTIFICATE_ACTIVATE = "/v1/certificate/activate";

    CertificateModel get(Long id) throws MicroServiceException;

    List<CertificateModel> list() throws MicroServiceException;

    CertificateModel create(CreateCertificateRequest request) throws MicroServiceException;

    CertificateModel update(CertificateModel request) throws MicroServiceException;

    Boolean delete(Long id) throws MicroServiceException;

    IssuedCertificateModel generate(Long certificateId) throws MicroServiceException;

    Boolean activate(String uuid) throws MicroServiceException;
}
