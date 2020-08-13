package com.thelak.smtp.services;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbUser;
import com.thelak.route.smtp.interfaces.IEmailService;
import com.thelak.route.smtp.models.SendEmailRequest;
import com.thelak.route.smtp.models.SendEmailResponse;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class EmailService extends AbstractMicroservice implements IEmailService {

    @Qualifier("getJavaMailSender")
    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    public SimpleMailMessage template;

    @Autowired
    DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @PostConstruct
    public void init() {
        objectContext = databaseService.getContext();
    }


    public SendEmailResponse sendSimpleMessage(SendEmailRequest request) {
        try {
            DbUser user = SelectById.query(DbUser.class, request.getUserId()).selectFirst(objectContext);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Thelak. Подтверждение регистрации.");
            message.setText("Подтвердите регистрация на аккаунт " + user.getEmail());
            emailSender.send(message);

            return SendEmailResponse.builder()
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error(e.getMessage());
            return SendEmailResponse.builder()
                    .success(false)
                    .build();
        }
    }
}

