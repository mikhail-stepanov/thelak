package com.thelak.route.payments.services;

import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.interfaces.IPaymentService;
import com.thelak.route.payments.models.certificate.BuyCertificateRequest;
import com.thelak.route.payments.models.cloudpayments.secure.SecureResponse;
import com.thelak.route.payments.models.subscription.BuySubscriptionRequest;
import com.thelak.route.payments.models.CardUpdateRequest;
import com.thelak.route.payments.models.PaymentsConfigModel;
import com.thelak.route.payments.models.cloudpayments.cryptogramm.CryptogrammPayResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

public class PaymentService extends BaseMicroservice implements IPaymentService {

    public PaymentService(RestTemplate restTemplate) {
        super("ms-payments", restTemplate);
    }

    @Override
    public Boolean buyCertificateRequest(BuyCertificateRequest buyCertificateRequest) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(PAYMENTS_CERT_REQ), buyCertificateRequest, Boolean.class).getBody());
    }

    @Override
    public CryptogrammPayResponse buySubscriptionRequest(BuySubscriptionRequest buySubscriptionRequest, HttpServletRequest request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(PAYMENTS_SUB_REQ), buySubscriptionRequest, CryptogrammPayResponse.class).getBody());
    }

    @Override
    public SecureResponse buySubscriptionConfirm(String MD, String PaRes) throws MicroServiceException {
        return null;
    }

    @Override
    public ModelAndView redirectBeforeConfirm(String MD, String PaRes) throws MicroServiceException {
        return null;
    }

    @Override
    public Boolean updateCardInfo(CardUpdateRequest cardUpdateRequest) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(PAYMENTS_UPDATE_CARD), cardUpdateRequest, Boolean.class).getBody());
    }

    @Override
    public Boolean cancelSubscription() throws MicroServiceException {
        return null;
    }

    @Override
    public PaymentsConfigModel getConfig() throws MicroServiceException {
        return null;
    }
}
