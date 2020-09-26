package com.thelak.route.payments.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.models.BuyCertificateRequest;
import com.thelak.route.payments.models.BuySubscriptionRequest;
import com.thelak.route.payments.models.CardUpdateRequest;

public interface IPaymentService {

    String PAYMENTS_CERT = "/v1/payments/cert";
    String PAYMENTS_SUB = "/v1/payments/sub";
    String PAYMENTS_UPDATE_CARD = "/v1/payments/card/update";

    Boolean buyCertificate(BuyCertificateRequest buyCertificateRequest) throws MicroServiceException;

    Boolean buySubscription(BuySubscriptionRequest buySubscriptionRequest) throws MicroServiceException;

    Boolean updateCardInfo(CardUpdateRequest cardUpdateRequest) throws MicroServiceException;

}

