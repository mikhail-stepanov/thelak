package com.thelak.route.payments.services;

import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.interfaces.IPaymentService;
import com.thelak.route.payments.models.BuyCertificateRequest;
import com.thelak.route.payments.models.BuySubscriptionRequest;
import com.thelak.route.payments.models.CardUpdateRequest;
import com.thelak.route.payments.models.PaymentsConfigModel;
import org.springframework.web.client.RestTemplate;

public class PaymentService extends BaseMicroservice implements IPaymentService {

    public PaymentService(RestTemplate restTemplate) {
        super("ms-payments", restTemplate);
    }

    @Override
    public Boolean buyCertificate(BuyCertificateRequest buyCertificateRequest) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(PAYMENTS_CERT), buyCertificateRequest, Boolean.class).getBody());
    }

    @Override
    public Boolean buySubscription(BuySubscriptionRequest buySubscriptionRequest) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(PAYMENTS_SUB), buySubscriptionRequest, Boolean.class).getBody());
    }

    @Override
    public Boolean updateCardInfo(CardUpdateRequest cardUpdateRequest) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(PAYMENTS_UPDATE_CARD), cardUpdateRequest, Boolean.class).getBody());
    }

    @Override
    public PaymentsConfigModel getConfig() throws MicroServiceException {
        return null;
    }
}
