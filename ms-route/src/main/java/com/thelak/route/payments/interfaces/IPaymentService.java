package com.thelak.route.payments.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.models.BuyCertificateRequest;
import com.thelak.route.payments.models.BuySubscriptionRequest;
import com.thelak.route.payments.models.CardUpdateRequest;
import com.thelak.route.payments.models.PaymentsConfigModel;
import com.thelak.route.payments.models.cloudpayments.cryptogramm.CryptogrammPayResponse;
import com.thelak.route.payments.models.cloudpayments.reccurent.ReccurentPayResponse;
import com.thelak.route.payments.models.cloudpayments.secure.ConfirmModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

public interface IPaymentService {

    String PAYMENTS_CERT_REQ = "/v1/payments/cert/req";
    String PAYMENTS_SUB_REQ = "/v1/payments/sub/req";
    String PAYMENTS_SUB_CONFIRM = "/v1/payments/sub/confirm";
    String PAYMENTS_REDIRECT = "/v1/payments/sub/redir";
    String PAYMENTS_UPDATE_CARD = "/v1/payments/card/update";
    String PAYMENTS_CONFIG = "/v1/payments/config";

    Boolean buyCertificateRequest(BuyCertificateRequest buyCertificateRequest) throws MicroServiceException;

    CryptogrammPayResponse buySubscriptionRequest(BuySubscriptionRequest buySubscriptionRequest,  HttpServletRequest request) throws MicroServiceException;

    ReccurentPayResponse buySubscriptionConfirm(String MD, String PaRes) throws MicroServiceException;

    ConfirmModel redirectBeforeConfirm(String MD, String PaRes) throws MicroServiceException;

    Boolean updateCardInfo(CardUpdateRequest cardUpdateRequest) throws MicroServiceException;

    PaymentsConfigModel getConfig() throws MicroServiceException;

}

