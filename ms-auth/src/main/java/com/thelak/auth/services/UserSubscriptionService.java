package com.thelak.auth.services;

import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbUser;
import com.thelak.route.message.IMessageService;
import com.thelak.route.payments.models.subscription.SetSubscriptionModel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Closeable;

@Service
public class UserSubscriptionService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private IMessageService messageService;

    private Closeable queueSubscriber;

    ObjectContext objectContext;

    @Value("${user.subscription.queue:#{null}}")
    private String userSubscriptionQueue;

    @PostConstruct
    void init() {
        objectContext = databaseService.getContext();
        queueSubscriber = messageService.subscribe(userSubscriptionQueue, 10, SetSubscriptionModel.class, this::handleMessage);
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

    private boolean handleMessage(SetSubscriptionModel message) {
        try {
            if (message != null) {

                DbUser dbUser = SelectById.query(DbUser.class, message.getUserId()).selectFirst(objectContext);
                dbUser.setSubscriptionDate(message.getSubscriptionDate());
                dbUser.setIsSubscribe(true);

                objectContext.commitChanges();

                return true;
            } else return false;
        } catch (Exception ex) {
            return false;
        }
    }
}
