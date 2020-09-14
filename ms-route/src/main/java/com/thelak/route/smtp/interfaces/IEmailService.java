package com.thelak.route.smtp.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.smtp.models.SendEmailRequest;
import com.thelak.route.smtp.models.SendEmailResponse;

public interface IEmailService {

    String EMAIL_SIMPLE = "/v1/email";
    String EMAIL_HTML = "/v1/email/html";

    Boolean sendMessage(SendEmailRequest request) throws MicroServiceException;

    Boolean sendHtmlMessage(String to, String subject, String htmlBody);
}
