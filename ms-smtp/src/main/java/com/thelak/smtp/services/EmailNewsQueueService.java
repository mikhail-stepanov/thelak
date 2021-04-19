package com.thelak.smtp.services;

import com.thelak.route.message.IMessageService;
import com.thelak.route.smtp.models.EmailAllRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.util.List;

@Service
public class EmailNewsQueueService {

    @Qualifier("getJavaMailSender")
    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    private IMessageService messageService;

    private Closeable queueSubscriber;

    @Value("${user.email.news.queue:news_email_queue}")
    private String userEmailQueue;

    @PostConstruct
    void init() {
        queueSubscriber = messageService.subscribeBatch(userEmailQueue, 1, EmailAllRequest.class, this::handleMessage);
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

    private boolean handleMessage(List<EmailAllRequest> model) {
        try {
            model.forEach(m -> {
                String subject = m.getSubject();
                String text = m.getHtmlBody();
                m.getTo().forEach(to -> {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(to);
                    message.setSubject(subject);
                    message.setText(text);
                    emailSender.send(message);
                });
            });

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
