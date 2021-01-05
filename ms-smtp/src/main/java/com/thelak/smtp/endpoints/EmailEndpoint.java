package com.thelak.smtp.endpoints;

import com.google.gson.Gson;
import com.thelak.core.endpoints.MicroserviceAdvice;
import com.thelak.core.services.MessageService;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbPaymentConfig;
import com.thelak.database.entity.DbSmtpTemplate;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.smtp.interfaces.IEmailService;
import com.thelak.route.smtp.models.EmailAllRequest;
import com.thelak.route.smtp.models.PartnerRequest;
import com.thelak.route.smtp.models.QuestionRequest;
import com.thelak.route.smtp.models.SendEmailRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@Api(value = "SMTP API", produces = "application/json")
public class EmailEndpoint extends MicroserviceAdvice implements IEmailService {

    @Qualifier("getJavaMailSender")
    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    public SimpleMailMessage template;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Autowired
    DatabaseService databaseService;

    @Override
    @RequestMapping(value = EMAIL_SIMPLE, method = {RequestMethod.POST})
    public Boolean sendMessage(@RequestBody SendEmailRequest request) {
        try {
            ObjectContext objectContext = databaseService.getContext();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getEmail());
            message.setSubject("Thelak. Подтверждение регистрации.");
            message.setText("Подтвердите регистрация на аккаунт " + request.getEmail());
            emailSender.send(message);

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    @RequestMapping(value = EMAIL_HTML, method = {RequestMethod.GET})
    public Boolean sendHtmlMessage(@RequestParam String to, @RequestParam String subject, @RequestParam String link, @RequestParam Long templateId) {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbSmtpTemplate smtpTemplate = SelectById.query(DbSmtpTemplate.class, templateId).selectFirst(objectContext);

            Context thymeleafContext = new Context();
            thymeleafContext.setVariable("link", link);
            String htmlBody = thymeleafTemplateEngine.process(smtpTemplate.getContent(), thymeleafContext);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            emailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    @ApiOperation(value = "Send partnership request by email")
    @RequestMapping(value = EMAIL_PARTNER_REQUEST, method = {RequestMethod.POST})
    public Boolean sendPartnerRequest(@RequestBody PartnerRequest request) throws MicroServiceException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("ineed@thelak.com");
            message.setSubject("Thelak. Партнерская заявка.");
            message.setText("Получен запрос на сотрудничество от - " + request.getEmail() + "(" + request.getFio() + ")" + "\n\nС текстом:\n" + request.getText() + "\n\n\nС уважением,\nКоманда Thelak");
            emailSender.send(message);

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    @ApiOperation(value = "Send question request by email")
    @RequestMapping(value = EMAIL_QUESTION, method = {RequestMethod.POST})
    public Boolean sendQuestion(@RequestBody QuestionRequest request) throws MicroServiceException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("ineed@thelak.com");
            message.setSubject("Thelak. Вопрос от пользователя.");
            message.setText("Получен вопрос от пользователя - " + request.getEmail() + "(" + request.getFio() + ")" + "\n\nТекст вопроса:\n" + request.getQuestion() + "\n\n\nС уважением,\nКоманда Thelak");
            emailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    @ApiOperation(value = "Send password restore request by email")
    @RequestMapping(value = EMAIL_PASSWORD, method = {RequestMethod.GET})
    public Boolean sendRestorePassword(@RequestParam String to, @RequestParam String link) throws MicroServiceException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Thelak. Восстановление Пароля.");
            message.setText("Уважаемый пользователь Thelak!\n\nМы получили запрос на восстановление пароля.\nДля восстановления перейдите по ссылке: " + "https://thelak.com/sign/recover/password?uuid=" + URLEncoder.encode(link, StandardCharsets.UTF_8) + "\n\n\nС уважением,\nКоманда Thelak");
            emailSender.send(message);

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


    @Override
    @ApiOperation(value = "Send email for group of users")
    @RequestMapping(value = EMAIL_ALL, method = {RequestMethod.POST})    public Boolean sendEmailForAll(EmailAllRequest request) throws MicroServiceException {
        return true;
    }
}
