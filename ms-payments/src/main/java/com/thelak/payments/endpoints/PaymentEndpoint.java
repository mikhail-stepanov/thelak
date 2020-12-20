package com.thelak.payments.endpoints;

import com.google.gson.Gson;
import com.thelak.core.endpoints.MicroserviceAdvice;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.*;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.exceptions.MsNotAuthorizedException;
import com.thelak.route.exceptions.MsObjectNotFoundException;
import com.thelak.route.message.IMessageService;
import com.thelak.route.payments.interfaces.IPaymentService;
import com.thelak.route.payments.models.CardUpdateRequest;
import com.thelak.route.payments.models.PaymentsConfigModel;
import com.thelak.route.payments.models.apple.ApplePayCertRequest;
import com.thelak.route.payments.models.apple.ApplePayStartSessionRequest;
import com.thelak.route.payments.models.apple.ApplePaySubRequest;
import com.thelak.route.payments.models.apple.AppleValidationRequest;
import com.thelak.route.payments.models.certificate.BuyCertificateRequest;
import com.thelak.route.payments.models.certificate.BuyCertificateResponse;
import com.thelak.route.payments.models.certificate.CertificateViewType;
import com.thelak.route.payments.models.certificate.IssuedCertificateModel;
import com.thelak.route.payments.models.cloudpayments.cancel.CancelRequest;
import com.thelak.route.payments.models.cloudpayments.cancel.CancelResponse;
import com.thelak.route.payments.models.cloudpayments.cryptogramm.CryptogrammPayRequest;
import com.thelak.route.payments.models.cloudpayments.cryptogramm.CryptogrammPayResponse;
import com.thelak.route.payments.models.cloudpayments.reccurent.RecurrentPayRequest;
import com.thelak.route.payments.models.cloudpayments.reccurent.RecurrentPayResponse;
import com.thelak.route.payments.models.cloudpayments.secure.SecureRequest;
import com.thelak.route.payments.models.cloudpayments.secure.SecureResponse;
import com.thelak.route.payments.models.promo.PromoModel;
import com.thelak.route.payments.models.subscription.BuySubscriptionRequest;
import com.thelak.route.payments.models.subscription.SetSubscriptionModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.thelak.payments.services.PaymentsHelper.buildCertificateModel;

@RestController
@Api(value = "Payment API", produces = "application/json")
public class PaymentEndpoint extends MicroserviceAdvice implements IPaymentService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private IMessageService messageService;

    @Value("${user.subscription.queue:#{null}}")
    private String userSubscriptionQueue;

    @Value("${user.certificate.queue:#{null}}")
    private String userCertificateQueue;

    RestTemplate restTemplate;

    Gson gson;

    @PostConstruct
    private void initialize() {
        ObjectContext objectContext = databaseService.getContext();
        DbPaymentConfig username = ObjectSelect.query(DbPaymentConfig.class)
                .where(DbPaymentConfig.NAME.eq("PUBLIC_ID")).selectFirst(objectContext);
        DbPaymentConfig password = ObjectSelect.query(DbPaymentConfig.class)
                .where(DbPaymentConfig.NAME.eq("SECRET_KEY")).selectFirst(objectContext);
        restTemplate = new RestTemplateBuilder()
                .basicAuthorization(username.getValue(), password.getValue())
                .build();

        gson = new Gson();

    }

    @Override
    @ApiOperation(value = "Buy certificate")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_CERT_REQ, method = {RequestMethod.POST})
    public CryptogrammPayResponse buyCertificateRequest(BuyCertificateRequest buyCertificateRequest, HttpServletRequest request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            UserInfo userInfo = null;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception e) {
                throw new MsNotAuthorizedException();
            }

            DbCertificate dbCertificate = SelectById.query(DbCertificate.class, buyCertificateRequest.getCertificateId())
                    .selectFirst(objectContext);

            DbIssuedCertificate dbIssuedCertificate = objectContext.newObject(DbIssuedCertificate.class);
            dbIssuedCertificate.setActive(true);
            dbIssuedCertificate.setActiveDate(LocalDateTime.now().plusMonths(1L));
            dbIssuedCertificate.setCreatedDate(LocalDateTime.now());
            dbIssuedCertificate.setUuid(UUID.randomUUID().toString());
            dbIssuedCertificate.setIssuedToCertificate(dbCertificate);
            dbIssuedCertificate.setFio(buyCertificateRequest.getFio());
            dbIssuedCertificate.setDescription(buyCertificateRequest.getDescription());
            dbIssuedCertificate.setType(buyCertificateRequest.getType().name());
            objectContext.commitChanges();

            CryptogrammPayRequest cryptogrammPayRequest = CryptogrammPayRequest.builder()
                    .AccountId(userInfo.getUserId())
                    .Amount(dbCertificate.getPrice())
                    .CardCryptogramPacket(buyCertificateRequest.getCardCryptogramPacket())
                    .Currency("RUB")
                    .Description("Покупка сертификата Thelak на " + dbCertificate.getMonths() + " месяцев.")
                    .Email(userInfo.getUserEmail())
                    .IpAddress(request.getRemoteAddr())
                    .Name(buyCertificateRequest.getCardName())
                    .build();

            DbPaymentConfig dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                    .where(DbPaymentConfig.NAME.eq("CRYPTOGRAMM_CHARGE_URL")).selectFirst(objectContext);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), cryptogrammPayRequest, String.class);
            CryptogrammPayResponse result = gson.fromJson(responseEntity.getBody(), CryptogrammPayResponse.class);
            DbPaymentsCryptogramm dbPaymentsCryptogramm = objectContext.newObject(DbPaymentsCryptogramm.class);
            dbPaymentsCryptogramm.setAmount(cryptogrammPayRequest.getAmount());
            dbPaymentsCryptogramm.setCardCryptogram(cryptogrammPayRequest.getCardCryptogramPacket());
            dbPaymentsCryptogramm.setCreatedDate(LocalDateTime.now());
            dbPaymentsCryptogramm.setCurrency(cryptogrammPayRequest.getCurrency());
            dbPaymentsCryptogramm.setDescription(cryptogrammPayRequest.getDescription());
            dbPaymentsCryptogramm.setIdUser(cryptogrammPayRequest.getAccountId());
            dbPaymentsCryptogramm.setName(cryptogrammPayRequest.getName());
            dbPaymentsCryptogramm.setStatus(false);
            dbPaymentsCryptogramm.setCryptogrammToCertificate(dbIssuedCertificate);
            dbPaymentsCryptogramm.setTransactionId(result.getModel().getTransactionId());

            objectContext.commitChanges();

            return result;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Buy certificate")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_CERT_CONFIRM, method = {RequestMethod.GET})
    public BuyCertificateResponse buyCertificateConfirm(@RequestParam String MD) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            UserInfo userInfo = null;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception e) {
                throw new MsNotAuthorizedException();
            }

            DbPaymentsCryptogramm dbPaymentsCryptogramm = ObjectSelect.query(DbPaymentsCryptogramm.class)
                    .where(DbPaymentsCryptogramm.TRANSACTION_ID.eq(Long.valueOf(MD)))
                    .selectFirst(objectContext);

            DbPaymentConfig dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                    .where(DbPaymentConfig.NAME.eq("3D_SECURE_URL")).selectFirst(objectContext);

            SecureRequest dSecureRequest = SecureRequest.builder()
                    .PaRes(dbPaymentsCryptogramm.getPares())
                    .TransactionId(Long.valueOf(MD))
                    .build();

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), dSecureRequest, String.class);
            SecureResponse secureResponse = gson.fromJson(responseEntity.getBody(), SecureResponse.class);

            if (secureResponse.getSuccess()) {
                dbPaymentsCryptogramm.setStatus(true);
                dbPaymentsCryptogramm.setModifiedDate(LocalDateTime.now());

                DbIssuedCertificate certificate = dbPaymentsCryptogramm.getCryptogrammToCertificate();

                DbPromo dbPromo = objectContext.newObject(DbPromo.class);
                dbPromo.setCode(certificate.getUuid());
                dbPromo.setMonths((int) certificate.getIssuedToCertificate().getMonths());

                objectContext.commitChanges();

                IssuedCertificateModel issuedCertificateModel = IssuedCertificateModel.builder()
                        .id((Long) certificate.getObjectId().getIdSnapshot().get("id"))
                        .buyerEmail(userInfo.getUserEmail())
                        .uuid(certificate.getUuid())
                        .fio(certificate.getFio())
                        .description(certificate.getDescription())
                        .active(certificate.isActive())
                        .activeDate(certificate.getActiveDate())
                        .type(CertificateViewType.valueOf(certificate.getType()))
                        .certificateModel(buildCertificateModel(certificate.getIssuedToCertificate()))
                        .build();

                messageService.publish(userCertificateQueue, issuedCertificateModel);

                return BuyCertificateResponse.builder()
                        .payResponse(secureResponse)
                        .certificate(issuedCertificateModel)
                        .build();
            }

            return BuyCertificateResponse.builder()
                    .payResponse(secureResponse)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Buy subscription confirm")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_SUB_CONFIRM, method = {RequestMethod.GET})
    public SecureResponse buySubscriptionConfirm(@RequestParam String MD) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            UserInfo userInfo = null;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception e) {
                throw new MsNotAuthorizedException();
            }

            DbPaymentsCryptogramm dbPaymentsCryptogramm = ObjectSelect.query(DbPaymentsCryptogramm.class)
                    .where(DbPaymentsCryptogramm.TRANSACTION_ID.eq(Long.valueOf(MD)))
                    .selectFirst(objectContext);

            DbPaymentConfig dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                    .where(DbPaymentConfig.NAME.eq("3D_SECURE_URL")).selectFirst(objectContext);

            SecureRequest dSecureRequest = SecureRequest.builder()
                    .PaRes(dbPaymentsCryptogramm.getPares())
                    .TransactionId(Long.valueOf(MD))
                    .build();

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), dSecureRequest, String.class);
            SecureResponse secureResponse = gson.fromJson(responseEntity.getBody(), SecureResponse.class);

            if (secureResponse.getSuccess()) {
                dbPaymentsCryptogramm.setStatus(true);
                dbPaymentsCryptogramm.setModifiedDate(LocalDateTime.now());
                objectContext.commitChanges();

                DbSubscription subscription = dbPaymentsCryptogramm.getCryptogrammToSubscription();
                messageService.publish(userSubscriptionQueue, SetSubscriptionModel.builder()
                        .userId(userInfo.getUserId())
                        .subType("SUBSCRIPTION")
                        .subscriptionDate(LocalDateTime.now().plusMonths(subscription.getMonths())).build());

                authenticationService.setSubscription(SetSubscriptionModel.builder()
                        .userId(userInfo.getUserId())
                        .subType("SUBSCRIPTION")
                        .subscriptionDate(LocalDateTime.now().plusMonths(subscription.getMonths())).build());

                try {
                    RecurrentPayRequest recurrentPayRequest = RecurrentPayRequest.builder()
                            .accountId(userInfo.getUserId().toString())
                            .amount(subscription.getPrice())
                            .currency("RUB")
                            .description("Подписка Thelak на " + subscription.getMonths() + " месяцев.")
                            .email(userInfo.getUserEmail())
                            .interval("Month")
                            .period(subscription.getMonths())
                            .startDate(LocalDateTime.now().plusMonths(subscription.getMonths()).toString())
                            .requireConfirmation(false)
                            .token(secureResponse.getModel().getToken())
                            .build();

                    DbPaymentsRecurrent dbPaymentsRecurrent = objectContext.newObject(DbPaymentsRecurrent.class);
                    dbPaymentsRecurrent.setIdUser(userInfo.getUserId());
                    dbPaymentsRecurrent.setAmount(recurrentPayRequest.getAmount());
                    dbPaymentsRecurrent.setCurrency(recurrentPayRequest.getCurrency());
                    dbPaymentsRecurrent.setDescription(recurrentPayRequest.getDescription());
                    dbPaymentsRecurrent.setEmail(recurrentPayRequest.getEmail());
                    dbPaymentsRecurrent.setInterval(recurrentPayRequest.getInterval());
                    dbPaymentsRecurrent.setPeriod(recurrentPayRequest.getPeriod());
                    dbPaymentsRecurrent.setStartDate(LocalDateTime.parse(recurrentPayRequest.getStartDate()));
                    dbPaymentsRecurrent.setRequireConfirmation(recurrentPayRequest.getRequireConfirmation());
                    dbPaymentsRecurrent.setToken(recurrentPayRequest.getToken());
                    dbPaymentsRecurrent.setStatus(false);
                    dbPaymentsRecurrent.setCreatedDate(LocalDateTime.now());
                    dbPaymentsRecurrent.setRecurrentToSubscription(subscription);
                    objectContext.commitChanges();

                    dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                            .where(DbPaymentConfig.NAME.eq("RECURRENT_URL")).selectFirst(objectContext);

                    responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), recurrentPayRequest, String.class);
                    RecurrentPayResponse recurrentPayResponse = gson.fromJson(responseEntity.getBody(), RecurrentPayResponse.class);
                    if (recurrentPayResponse.getSuccess()) {
                        dbPaymentsRecurrent.setStatus(true);
                        dbPaymentsRecurrent.setIdRecurrent(recurrentPayResponse.getModel().getId());
                        objectContext.commitChanges();
                    }
                } catch (Exception ignored) {
                }
            }
            return secureResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Start Apple Pay session")
    @RequestMapping(value = PAYMENTS_APPLE, method = {RequestMethod.POST})
    public ResponseEntity<String> applePayStartSession(@RequestBody ApplePayStartSessionRequest request) {
        ObjectContext objectContext = databaseService.getContext();

        AppleValidationRequest appleValidationRequest = AppleValidationRequest.builder()
                .ValidationUrl(request.getValidationUrl())
                .build();

        DbPaymentConfig dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                .where(DbPaymentConfig.NAME.eq("APPLE_PAY_URL")).selectFirst(objectContext);

        return restTemplate.postForEntity(dbPaymentConfig.getValue(), appleValidationRequest, String.class);
    }

    @Override
    @ApiOperation(value = "Buy subscription (Apple Pay)")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_SUB_APPLE, method = {RequestMethod.POST})
    public CryptogrammPayResponse buySubscriptionApplePay(@RequestBody ApplePaySubRequest request, HttpServletRequest httpRequest) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            UserInfo userInfo;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception e) {
                throw new MsNotAuthorizedException();
            }

            DbSubscription dbSubscription = SelectById.query(DbSubscription.class, request.getSubscriptionId())
                    .selectFirst(objectContext);

            CryptogrammPayRequest cryptogrammPayRequest = CryptogrammPayRequest.builder()
                    .AccountId(userInfo.getUserId())
                    .Amount(dbSubscription.getPrice())
                    .CardCryptogramPacket(request.getCryptogram())
                    .Currency("RUB")
                    .Description("Покупка подписки Thelak на " + dbSubscription.getMonths() + " месяцев.")
                    .Email(userInfo.getUserEmail())
                    .IpAddress(httpRequest.getRemoteAddr())
                    .build();

            DbPaymentConfig dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                    .where(DbPaymentConfig.NAME.eq("CRYPTOGRAMM_CHARGE_URL")).selectFirst(objectContext);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), cryptogrammPayRequest, String.class);
            CryptogrammPayResponse result = gson.fromJson(responseEntity.getBody(), CryptogrammPayResponse.class);
            DbPaymentsCryptogramm dbPaymentsCryptogramm = objectContext.newObject(DbPaymentsCryptogramm.class);
            dbPaymentsCryptogramm.setAmount(cryptogrammPayRequest.getAmount());
            dbPaymentsCryptogramm.setCardCryptogram(cryptogrammPayRequest.getCardCryptogramPacket());
            dbPaymentsCryptogramm.setCreatedDate(LocalDateTime.now());
            dbPaymentsCryptogramm.setCurrency(cryptogrammPayRequest.getCurrency());
            dbPaymentsCryptogramm.setDescription(cryptogrammPayRequest.getDescription());
            dbPaymentsCryptogramm.setCryptogrammToSubscription(dbSubscription);
            dbPaymentsCryptogramm.setIdUser(cryptogrammPayRequest.getAccountId());
            dbPaymentsCryptogramm.setName(cryptogrammPayRequest.getName());
            dbPaymentsCryptogramm.setStatus(false);
            dbPaymentsCryptogramm.setCryptogrammToSubscription(dbSubscription);
            dbPaymentsCryptogramm.setTransactionId(result.getModel().getTransactionId());

            objectContext.commitChanges();

            if (result.getSuccess()) {
                dbPaymentsCryptogramm.setStatus(true);
                dbPaymentsCryptogramm.setModifiedDate(LocalDateTime.now());
                objectContext.commitChanges();

                DbSubscription subscription = dbPaymentsCryptogramm.getCryptogrammToSubscription();
                messageService.publish(userSubscriptionQueue, SetSubscriptionModel.builder()
                        .userId(userInfo.getUserId())
                        .subType("SUBSCRIPTION")
                        .subscriptionDate(LocalDateTime.now().plusMonths(subscription.getMonths())).build());

                authenticationService.setSubscription(SetSubscriptionModel.builder()
                        .userId(userInfo.getUserId())
                        .subType("SUBSCRIPTION")
                        .subscriptionDate(LocalDateTime.now().plusMonths(subscription.getMonths())).build());

                try {
                    RecurrentPayRequest recurrentPayRequest = RecurrentPayRequest.builder()
                            .accountId(userInfo.getUserId().toString())
                            .amount(subscription.getPrice())
                            .currency("RUB")
                            .description("Подписка Thelak на " + subscription.getMonths() + " месяцев.")
                            .email(userInfo.getUserEmail())
                            .interval("Month")
                            .period(subscription.getMonths())
                            .startDate(LocalDateTime.now().plusMonths(subscription.getMonths()).toString())
                            .requireConfirmation(false)
                            .token(result.getModel().getToken())
                            .build();

                    DbPaymentsRecurrent dbPaymentsRecurrent = objectContext.newObject(DbPaymentsRecurrent.class);
                    dbPaymentsRecurrent.setIdUser(userInfo.getUserId());
                    dbPaymentsRecurrent.setAmount(recurrentPayRequest.getAmount());
                    dbPaymentsRecurrent.setCurrency(recurrentPayRequest.getCurrency());
                    dbPaymentsRecurrent.setDescription(recurrentPayRequest.getDescription());
                    dbPaymentsRecurrent.setEmail(recurrentPayRequest.getEmail());
                    dbPaymentsRecurrent.setInterval(recurrentPayRequest.getInterval());
                    dbPaymentsRecurrent.setPeriod(recurrentPayRequest.getPeriod());
                    dbPaymentsRecurrent.setStartDate(LocalDateTime.parse(recurrentPayRequest.getStartDate()));
                    dbPaymentsRecurrent.setRequireConfirmation(recurrentPayRequest.getRequireConfirmation());
                    dbPaymentsRecurrent.setToken(recurrentPayRequest.getToken());
                    dbPaymentsRecurrent.setStatus(false);
                    dbPaymentsRecurrent.setCreatedDate(LocalDateTime.now());
                    dbPaymentsRecurrent.setRecurrentToSubscription(subscription);
                    objectContext.commitChanges();

                    dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                            .where(DbPaymentConfig.NAME.eq("RECURRENT_URL")).selectFirst(objectContext);

                    responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), recurrentPayRequest, String.class);
                    RecurrentPayResponse recurrentPayResponse = gson.fromJson(responseEntity.getBody(), RecurrentPayResponse.class);
                    if (recurrentPayResponse.getSuccess()) {
                        dbPaymentsRecurrent.setStatus(true);
                        dbPaymentsRecurrent.setIdRecurrent(recurrentPayResponse.getModel().getId());
                        objectContext.commitChanges();
                    }
                } catch (Exception ignored) {
                }
            }
            return result;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Buy certificate (Apple Pay)")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_CERT_APPLE, method = {RequestMethod.POST})
    public BuyCertificateResponse buyCertificateApplePay(@RequestBody ApplePayCertRequest request, HttpServletRequest httpRequest) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            UserInfo userInfo;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception e) {
                throw new MsNotAuthorizedException();
            }

            DbCertificate dbCertificate = SelectById.query(DbCertificate.class, request.getCertificateId())
                    .selectFirst(objectContext);

            DbIssuedCertificate dbIssuedCertificate = objectContext.newObject(DbIssuedCertificate.class);
            dbIssuedCertificate.setActive(true);
            dbIssuedCertificate.setActiveDate(LocalDateTime.now().plusMonths(1L));
            dbIssuedCertificate.setCreatedDate(LocalDateTime.now());
            dbIssuedCertificate.setUuid(UUID.randomUUID().toString());
            dbIssuedCertificate.setIssuedToCertificate(dbCertificate);
            dbIssuedCertificate.setFio(request.getFio());
            dbIssuedCertificate.setDescription(request.getDescription());
            dbIssuedCertificate.setType(request.getType().name());
            objectContext.commitChanges();

            CryptogrammPayRequest cryptogrammPayRequest = CryptogrammPayRequest.builder()
                    .AccountId(userInfo.getUserId())
                    .Amount(dbCertificate.getPrice())
                    .CardCryptogramPacket(request.getCryptogram())
                    .Currency("RUB")
                    .Description("Покупка сертификата Thelak на " + dbCertificate.getMonths() + " месяцев.")
                    .Email(userInfo.getUserEmail())
                    .IpAddress(httpRequest.getRemoteAddr())
                    .build();

            DbPaymentConfig dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                    .where(DbPaymentConfig.NAME.eq("CRYPTOGRAMM_CHARGE_URL")).selectFirst(objectContext);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), cryptogrammPayRequest, String.class);
            CryptogrammPayResponse result = gson.fromJson(responseEntity.getBody(), CryptogrammPayResponse.class);
            DbPaymentsCryptogramm dbPaymentsCryptogramm = objectContext.newObject(DbPaymentsCryptogramm.class);
            dbPaymentsCryptogramm.setAmount(cryptogrammPayRequest.getAmount());
            dbPaymentsCryptogramm.setCardCryptogram(cryptogrammPayRequest.getCardCryptogramPacket());
            dbPaymentsCryptogramm.setCreatedDate(LocalDateTime.now());
            dbPaymentsCryptogramm.setCurrency(cryptogrammPayRequest.getCurrency());
            dbPaymentsCryptogramm.setDescription(cryptogrammPayRequest.getDescription());
            dbPaymentsCryptogramm.setIdUser(cryptogrammPayRequest.getAccountId());
            dbPaymentsCryptogramm.setName(cryptogrammPayRequest.getName());
            dbPaymentsCryptogramm.setStatus(false);
            dbPaymentsCryptogramm.setCryptogrammToCertificate(dbIssuedCertificate);
            dbPaymentsCryptogramm.setTransactionId(result.getModel().getTransactionId());

            objectContext.commitChanges();

            if (result.getSuccess()) {
                dbPaymentsCryptogramm.setStatus(true);
                dbPaymentsCryptogramm.setModifiedDate(LocalDateTime.now());

                DbIssuedCertificate certificate = dbPaymentsCryptogramm.getCryptogrammToCertificate();

                DbPromo dbPromo = objectContext.newObject(DbPromo.class);
                dbPromo.setCode(certificate.getUuid());
                dbPromo.setMonths((int) certificate.getIssuedToCertificate().getMonths());

                objectContext.commitChanges();

                return BuyCertificateResponse.builder()
                        .success(result.getSuccess())
                        .certificate(IssuedCertificateModel.builder()
                                .id((Long) certificate.getObjectId().getIdSnapshot().get("id"))
                                .uuid(certificate.getUuid())
                                .fio(certificate.getFio())
                                .description(certificate.getDescription())
                                .active(certificate.isActive())
                                .activeDate(certificate.getActiveDate())
                                .type(CertificateViewType.valueOf(certificate.getType()))
                                .certificateModel(buildCertificateModel(certificate.getIssuedToCertificate()))
                                .build())
                        .build();

            }

            return BuyCertificateResponse.builder()
                    .success(result.getSuccess())
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Redirect before confirm")
    @RequestMapping(value = PAYMENTS_REDIRECT, method = {RequestMethod.POST})
    public ModelAndView redirectBeforeConfirm(@RequestParam String MD, @RequestParam String PaRes, HttpServletRequest request) throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();
        DbPaymentsCryptogramm dbPaymentsCryptogramm = ObjectSelect.query(DbPaymentsCryptogramm.class)
                .where(DbPaymentsCryptogramm.TRANSACTION_ID.eq(Long.valueOf(MD)))
                .selectFirst(objectContext);
        dbPaymentsCryptogramm.setPares(PaRes);
        objectContext.commitChanges();
        if (dbPaymentsCryptogramm.getCryptogrammToCertificate() != null) {
            RedirectView rv = new RedirectView();
            rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            rv.setUrl("https://thelak.com/pay/confirm/cert/?MD=" + MD);
            return new ModelAndView(rv);
        } else {
            RedirectView rv = new RedirectView();
            rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            rv.setUrl("https://thelak.com/pay/confirm/sub/?MD=" + MD);
            return new ModelAndView(rv);
        }
    }

    @Override
    @ApiOperation(value = "Buy subscription request")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_SUB_REQ, method = {RequestMethod.POST})
    public CryptogrammPayResponse buySubscriptionRequest(BuySubscriptionRequest buySubscriptionRequest, HttpServletRequest request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();
            UserInfo userInfo;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception e) {
                throw new MsNotAuthorizedException();
            }

            DbSubscription dbSubscription = SelectById.query(DbSubscription.class, buySubscriptionRequest.getSubscriptionId())
                    .selectFirst(objectContext);

            CryptogrammPayRequest cryptogrammPayRequest = CryptogrammPayRequest.builder()
                    .AccountId(userInfo.getUserId())
                    .Amount(dbSubscription.getPrice())
                    .CardCryptogramPacket(buySubscriptionRequest.getCardCryptogramPacket())
                    .Currency("RUB")
                    .Description("Покупка подписки Thelak на " + dbSubscription.getMonths() + " месяцев.")
                    .Email(userInfo.getUserEmail())
                    .IpAddress(request.getRemoteAddr())
                    .Name(buySubscriptionRequest.getCardName())
                    .build();

            DbPaymentConfig dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                    .where(DbPaymentConfig.NAME.eq("CRYPTOGRAMM_CHARGE_URL")).selectFirst(objectContext);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), cryptogrammPayRequest, String.class);
            CryptogrammPayResponse result = gson.fromJson(responseEntity.getBody(), CryptogrammPayResponse.class);
            DbPaymentsCryptogramm dbPaymentsCryptogramm = objectContext.newObject(DbPaymentsCryptogramm.class);
            dbPaymentsCryptogramm.setAmount(cryptogrammPayRequest.getAmount());
            dbPaymentsCryptogramm.setCardCryptogram(cryptogrammPayRequest.getCardCryptogramPacket());
            dbPaymentsCryptogramm.setCreatedDate(LocalDateTime.now());
            dbPaymentsCryptogramm.setCurrency(cryptogrammPayRequest.getCurrency());
            dbPaymentsCryptogramm.setDescription(cryptogrammPayRequest.getDescription());
            dbPaymentsCryptogramm.setCryptogrammToSubscription(dbSubscription);
            dbPaymentsCryptogramm.setIdUser(cryptogrammPayRequest.getAccountId());
            dbPaymentsCryptogramm.setName(cryptogrammPayRequest.getName());
            dbPaymentsCryptogramm.setStatus(false);
            dbPaymentsCryptogramm.setCryptogrammToSubscription(dbSubscription);
            dbPaymentsCryptogramm.setTransactionId(result.getModel().getTransactionId());

            objectContext.commitChanges();
            return result;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Update card info")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_UPDATE_CARD, method = {RequestMethod.POST})
    public Boolean updateCardInfo(CardUpdateRequest cardUpdateRequest) throws MicroServiceException {
        return null;
    }

    @Override
    @ApiOperation(value = "Cancel subscription")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_SUB_CANCEL, method = {RequestMethod.GET})
    public Boolean cancelSubscription() throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();
        UserInfo userInfo;
        try {
            userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
        } catch (Exception e) {
            throw new MsNotAuthorizedException();
        }

        DbPaymentConfig dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                .where(DbPaymentConfig.NAME.eq("RECURRENT_CANCEL_URL")).selectFirst(objectContext);

        DbPaymentsRecurrent dbPaymentsRecurrent = ObjectSelect.query(DbPaymentsRecurrent.class)
                .where(DbPaymentsRecurrent.ID_USER.eq(userInfo.getUserId()))
                .and(DbPaymentsRecurrent.STATUS.eq(true))
                .selectFirst(objectContext);

        CancelRequest cancelRequest = CancelRequest.builder()
                .Id(dbPaymentsRecurrent.getIdRecurrent()).build();

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), cancelRequest, String.class);
        CancelResponse cancelResponse = gson.fromJson(responseEntity.getBody(), CancelResponse.class);

        return cancelResponse.getSuccess();
    }

    @Override
    @ApiOperation(value = "Get cloudpayments public_id")
    @RequestMapping(value = PAYMENTS_CONFIG, method = {RequestMethod.GET})
    public PaymentsConfigModel getConfig() throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();
        DbPaymentConfig dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                .where(DbPaymentConfig.NAME.eq("PUBLIC_ID")).selectFirst(objectContext);

        return PaymentsConfigModel.builder()
                .name(dbPaymentConfig.getName())
                .value(dbPaymentConfig.getValue())
                .build();
    }

    @Override
    @ApiOperation(value = "Enter promo")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_PROMO_ENTER, method = {RequestMethod.POST})
    public PromoModel enterPromo(@RequestParam String code) throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();

        UserInfo userInfo;
        try {
            userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
        } catch (Exception e) {
            throw new MsNotAuthorizedException();
        }

        try {
            DbPromo dbPromo = null;
            try {
                dbPromo = ObjectSelect.query(DbPromo.class)
                        .where(DbPromo.CODE.lower().eq(code.toLowerCase())).selectFirst(objectContext);
            } catch (Exception e){
                throw new MsObjectNotFoundException("Can't find certificate or promo: ", code);
            }
            try {
                DbPromoEmail dbPromoEmail = ObjectSelect.query(DbPromoEmail.class)
                        .where(DbPromoEmail.EMAIL_TO_PROMO.eq(dbPromo))
                        .selectFirst(objectContext);
                if (dbPromoEmail != null) {
                    DbPromoEmail dbPromoEmailcheck = ObjectSelect.query(DbPromoEmail.class)
                            .where(DbPromoEmail.EMAIL_TO_PROMO.eq(dbPromo))
                            .and(DbPromoEmail.EMAIL.eq(userInfo.getUserEmail()))
                            .and(DbPromoEmail.ACTIVE.isTrue())
                            .selectFirst(objectContext);
                    if (dbPromoEmailcheck != null) {
                        messageService.publish(userSubscriptionQueue, SetSubscriptionModel.builder()
                                .userId(userInfo.getUserId())
                                .subType("PROMO")
                                .subscriptionDate(LocalDateTime.now().plusMonths(dbPromo.getMonths())).build());

                        authenticationService.setSubscription(SetSubscriptionModel.builder()
                                .userId(userInfo.getUserId())
                                .subType("PROMO")
                                .subscriptionDate(LocalDateTime.now().plusMonths(dbPromo.getMonths())).build());

                        dbPromoEmailcheck.setActive(false);
                        objectContext.commitChanges();

                        return PromoModel.builder()
                                .success(true)
                                .months(dbPromo.getMonths())
                                .description(dbPromo.getDescription())
                                .build();
                    }
                    return PromoModel.builder()
                            .success(false)
                            .months(dbPromo.getMonths())
                            .description(dbPromo.getDescription())
                            .build();
                }

            } catch (Exception ignored) {
            }
            messageService.publish(userSubscriptionQueue, SetSubscriptionModel.builder()
                    .userId(userInfo.getUserId())
                    .subType("PROMO")
                    .subscriptionDate(LocalDateTime.now().plusMonths(dbPromo.getMonths())).build());
            authenticationService.setSubscription(SetSubscriptionModel.builder()
                    .userId(userInfo.getUserId())
                    .subType("PROMO")
                    .subscriptionDate(LocalDateTime.now().plusMonths(dbPromo.getMonths())).build());

            return PromoModel.builder()
                    .success(true)
                    .months(dbPromo.getMonths())
                    .description(dbPromo.getDescription())
                    .build();
        } catch (Exception e) {
        }
        try {
            DbIssuedCertificate dbIssuedCertificate = ObjectSelect.query(DbIssuedCertificate.class)
                    .where(DbIssuedCertificate.UUID.lower().eq(code.toLowerCase())).selectFirst(objectContext);

            if (dbIssuedCertificate.isActive()) {

                messageService.publish(userSubscriptionQueue, SetSubscriptionModel.builder()
                        .userId(userInfo.getUserId())
                        .subType("CERTIFICATE")
                        .subscriptionDate(LocalDateTime.now().plusMonths(dbIssuedCertificate.getIssuedToCertificate().getMonths())).build());

                authenticationService.setSubscription(SetSubscriptionModel.builder()
                        .userId(userInfo.getUserId())
                        .subType("CERTIFICATE")
                        .subscriptionDate(LocalDateTime.now().plusMonths(dbIssuedCertificate.getIssuedToCertificate().getMonths())).build());

                dbIssuedCertificate.setActive(false);
                objectContext.commitChanges();

                return PromoModel.builder()
                        .success(true)
                        .months((int) dbIssuedCertificate.getIssuedToCertificate().getMonths())
                        .description(dbIssuedCertificate.getDescription())
                        .build();

            }
            return PromoModel.builder()
                    .success(false)
                    .build();
        } catch (Exception e) {
            throw new MsObjectNotFoundException("Can't find certificate or promo: ", code);
        }

    }
}
