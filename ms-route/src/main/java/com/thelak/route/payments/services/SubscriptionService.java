package com.thelak.route.payments.services;

import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.interfaces.ISubscriptionService;
import com.thelak.route.payments.models.subscription.SubscriptionModel;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SubscriptionService extends BaseMicroservice implements ISubscriptionService {

    public SubscriptionService(RestTemplate restTemplate) {
        super("ms-payments", restTemplate);
    }

    @Override
    public SubscriptionModel get(Long id) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(SUBSCRIPTION_GET), SubscriptionModel.class, id).getBody());
    }

    @Override
    public List<SubscriptionModel> list() throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(SUBSCRIPTION_LIST), List.class).getBody());
    }
}
