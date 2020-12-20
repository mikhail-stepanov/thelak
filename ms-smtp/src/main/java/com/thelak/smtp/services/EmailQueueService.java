package com.thelak.smtp.services;

import com.thelak.route.message.IMessageService;
import com.thelak.route.payments.models.certificate.IssuedCertificateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EmailQueueService {

    @Qualifier("getJavaMailSender")
    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    private IMessageService messageService;

    private Closeable queueSubscriber;

    @Value("${user.cetificate.queue:#{null}}")
    private String userCertificateQueue;

    @PostConstruct
    void init() {
        queueSubscriber = messageService.subscribe(userCertificateQueue, 10, IssuedCertificateModel.class, this::handleMessage);
    }

    @PreDestroy
    private void destroy() {
        try {
            if (queueSubscriber != null)
                queueSubscriber.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private boolean handleMessage(IssuedCertificateModel model) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(model.getBuyerEmail());
            message.setSubject("Thelak. Сертификат.");
            message.setText("Уважаемый пользователь Thelak!\n\nВы приобрели сертификат на подписку.\nДля его просмотра перейдите по ссылке: " + "https://thelak.com/cert/view?uuid=" + URLEncoder.encode(model.getUuid(), StandardCharsets.UTF_8) + "\n\n\nС уважением,\nКоманда Thelak");
            emailSender.send(message);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
