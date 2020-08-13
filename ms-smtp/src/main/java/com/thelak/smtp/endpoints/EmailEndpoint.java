package com.thelak.smtp.endpoints;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.smtp.interfaces.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.thelak.route.smtp.models.SendEmailRequest;
import com.thelak.route.smtp.models.SendEmailResponse;
import com.thelak.smtp.services.EmailService;

@RestController
public class EmailEndpoint implements IEmailService {

    @Autowired
    EmailService emailService;

    @Override
    @RequestMapping(value = EMAIL_SIMPLE, method = {RequestMethod.POST})
    public SendEmailResponse sendSimpleMessage(@RequestBody SendEmailRequest request) throws MicroServiceException {
        return emailService.sendSimpleMessage(request);
    }

}
