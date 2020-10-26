package com.thelak.smtp.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbSmtpTemplate;
import com.thelak.database.entity.DbUser;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.smtp.interfaces.IEmailService;
import com.thelak.route.smtp.models.PartnerRequest;
import com.thelak.route.smtp.models.QuestionRequest;
import com.thelak.route.smtp.models.SendEmailRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@RestController
@Api(value = "SMTP API", produces = "application/json")
public class EmailEndpoint extends AbstractMicroservice implements IEmailService {

    @Qualifier("getJavaMailSender")
    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    public SimpleMailMessage template;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Autowired
    DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(EmailEndpoint.class);

    @PostConstruct
    public void init() {
        objectContext = databaseService.getContext();
    }

    @Override
    @RequestMapping(value = EMAIL_SIMPLE, method = {RequestMethod.POST})
    public Boolean sendMessage(@RequestBody SendEmailRequest request) {
        try {

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
    @RequestMapping(value = EMAIL_CERTIFICATE, method = {RequestMethod.GET})
    public Boolean sendCert(@RequestParam String to, @RequestParam Long templateId,
                            @RequestParam String promo, @RequestParam String fio, @RequestParam String description) throws MicroServiceException {
        try {
            DbSmtpTemplate smtpTemplate = SelectById.query(DbSmtpTemplate.class, templateId).selectFirst(objectContext);

            Context thymeleafContext = new Context();
            thymeleafContext.setVariable("promo", promo);
            thymeleafContext.setVariable("fio", fio);
            thymeleafContext.setVariable("description", description);

            String htmlBody = thymeleafTemplateEngine.process(smtpTemplate.getContent(), thymeleafContext);
            System.out.println("!!!!!!!!!!!!!!!\n" + htmlBody);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            helper.setTo(to);
            helper.setSubject("Сертификат Thelak");
            helper.setText(htmlBody, true);
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
    public Boolean sendPartnerRequest(PartnerRequest request) throws MicroServiceException {
        return true;
    }

    @Override
    @ApiOperation(value = "Send question request by email")
    @RequestMapping(value = EMAIL_QUESTION, method = {RequestMethod.POST})
    public Boolean sendQuestion(QuestionRequest request) throws MicroServiceException {
        return true;
    }
}
