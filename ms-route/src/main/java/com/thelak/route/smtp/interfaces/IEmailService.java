package com.thelak.route.smtp.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.smtp.models.PartnerRequest;
import com.thelak.route.smtp.models.QuestionRequest;
import com.thelak.route.smtp.models.SendEmailRequest;
import com.thelak.route.smtp.models.SendEmailResponse;

import java.util.Map;

public interface IEmailService {

    String EMAIL_SIMPLE = "/v1/email";
    String EMAIL_HTML = "/v1/email/html";

    String EMAIL_PARTNER_REQUEST = "/v1/email/partner";
    String EMAIL_QUESTION = "/v1/email/question";

    Boolean sendMessage(SendEmailRequest request) throws MicroServiceException;

    Boolean sendHtmlMessage(String to, String subject, String link, Long templateId) throws MicroServiceException;

    Boolean sendPartnerRequest(PartnerRequest request) throws MicroServiceException;

    Boolean sendQuestion(QuestionRequest request) throws MicroServiceException;
}
