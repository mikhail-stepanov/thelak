package com.thelak.route.payments.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.models.CardUpdateRequest;
import com.thelak.route.payments.models.PaymentsConfigModel;
import com.thelak.route.payments.models.apple.ApplePayCertRequest;
import com.thelak.route.payments.models.apple.ApplePayStartSessionRequest;
import com.thelak.route.payments.models.apple.ApplePaySubRequest;
import com.thelak.route.payments.models.certificate.BuyCertificateRequest;
import com.thelak.route.payments.models.certificate.BuyCertificateResponse;
import com.thelak.route.payments.models.cloudpayments.cryptogramm.CryptogrammPayResponse;
import com.thelak.route.payments.models.cloudpayments.secure.SecureResponse;
import com.thelak.route.payments.models.promo.PromoModel;
import com.thelak.route.payments.models.subscription.BuySubscriptionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

public interface IPaymentService {

    String PAYMENTS_CERT_REQ = "/v1/payments/cert/req";
    String PAYMENTS_CERT_CONFIRM = "/v1/payments/cert/confirm/";
    String PAYMENTS_CERT_APPLE = "/v1/payments/cert/apple/";
    String PAYMENTS_SUB_REQ = "/v1/payments/sub/req";
    String PAYMENTS_SUB_CONFIRM = "/v1/payments/sub/confirm/";
    String PAYMENTS_APPLE = "/v1/payments/apple/start";
    String PAYMENTS_SUB_APPLE = "/v1/payments/sub/apple/";
    String PAYMENTS_SUB_CANCEL = "/v1/payments/sub/cancel";
    String PAYMENTS_REDIRECT = "/v1/payments/redir";
    String PAYMENTS_UPDATE_CARD = "/v1/payments/card/update";
    String PAYMENTS_CONFIG = "/v1/payments/config";

    String PAYMENTS_PROMO_ENTER = "/v1/payments/promo/enter";

    CryptogrammPayResponse buyCertificateRequest(BuyCertificateRequest buyCertificateRequest, HttpServletRequest request) throws MicroServiceException;

    BuyCertificateResponse buyCertificateConfirm(String MD) throws MicroServiceException;

    CryptogrammPayResponse buySubscriptionRequest(BuySubscriptionRequest buySubscriptionRequest, HttpServletRequest request) throws MicroServiceException;

    SecureResponse buySubscriptionConfirm(String MD) throws MicroServiceException;

    ResponseEntity<String> applePayStartSession(ApplePayStartSessionRequest request);

    CryptogrammPayResponse buySubscriptionApplePay(ApplePaySubRequest request, HttpServletRequest httpRequest) throws MicroServiceException;

    BuyCertificateResponse buyCertificateApplePay(ApplePayCertRequest request, HttpServletRequest httpRequest) throws MicroServiceException;

    ModelAndView redirectBeforeConfirm(String MD, String PaRes, HttpServletRequest request) throws MicroServiceException;

    Boolean updateCardInfo(CardUpdateRequest cardUpdateRequest) throws MicroServiceException;

    Boolean cancelSubscription() throws MicroServiceException;

    PaymentsConfigModel getConfig() throws MicroServiceException;

    PromoModel enterPromo(String code) throws MicroServiceException;

}

