package com.thelak.route.smtp.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.smtp.models.*;

import java.util.Map;

public interface IEmailService {

    String EMAIL_SIMPLE = "/v1/email";
    String EMAIL_HTML = "/v1/email/html";
    String EMAIL_CERTIFICATE = "/v1/email/cert";

    String EMAIL_PASSWORD = "/v1/email/password";
    String EMAIL_PARTNER_REQUEST = "/v1/email/partner";
    String EMAIL_QUESTION = "/v1/email/question";

    Boolean sendMessage(SendEmailRequest request) throws MicroServiceException;

    Boolean sendCert(String to, Long templateId, String promo, String fio, String description) throws MicroServiceException;

    Boolean sendHtmlMessage(String to, String subject, String link, Long templateId) throws MicroServiceException;

    Boolean sendPartnerRequest(PartnerRequest request) throws MicroServiceException;

    Boolean sendQuestion(QuestionRequest request) throws MicroServiceException;

    Boolean sendRestorePassword(String to, String link) throws MicroServiceException;

}
