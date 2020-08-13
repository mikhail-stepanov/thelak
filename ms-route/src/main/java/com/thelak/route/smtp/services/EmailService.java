package com.thelak.route.smtp.services;

import com.thelak.route.auth.models.UserModel;
import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.smtp.interfaces.IEmailService;
import com.thelak.route.smtp.models.SendEmailRequest;
import com.thelak.route.smtp.models.SendEmailResponse;
import org.springframework.web.client.RestTemplate;

public class EmailService extends BaseMicroservice implements IEmailService {

    public EmailService(RestTemplate restTemplate) {
        super("ms-smtp", restTemplate);
    }

    @Override
    public SendEmailResponse sendSimpleMessage(SendEmailRequest request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(EMAIL_SIMPLE), request, SendEmailResponse.class).getBody());

    }
}
