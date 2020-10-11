package com.thelak.payments.endpoints;

import com.google.gson.Gson;
import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbPaymentConfig;
import com.thelak.database.entity.DbPaymentsCryptogramm;
import com.thelak.database.entity.DbPaymentsRecurrent;
import com.thelak.database.entity.DbSubscription;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.exceptions.MsNotAuthorizedException;
import com.thelak.route.message.IMessageService;
import com.thelak.route.payments.interfaces.IPaymentService;
import com.thelak.route.payments.models.CardUpdateRequest;
import com.thelak.route.payments.models.PaymentsConfigModel;
import com.thelak.route.payments.models.certificate.BuyCertificateRequest;
import com.thelak.route.payments.models.cloudpayments.cancel.CancelRequest;
import com.thelak.route.payments.models.cloudpayments.cancel.CancelResponse;
import com.thelak.route.payments.models.cloudpayments.cryptogramm.CryptogrammPayRequest;
import com.thelak.route.payments.models.cloudpayments.cryptogramm.CryptogrammPayResponse;
import com.thelak.route.payments.models.cloudpayments.reccurent.RecurrentPayRequest;
import com.thelak.route.payments.models.cloudpayments.reccurent.RecurrentPayResponse;
import com.thelak.route.payments.models.cloudpayments.secure.SecureRequest;
import com.thelak.route.payments.models.cloudpayments.secure.SecureResponse;
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
import java.time.ZonedDateTime;

@RestController
@Api(value = "Payment API", produces = "application/json")
public class PaymentEndpoint extends AbstractMicroservice implements IPaymentService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private IMessageService messageService;

    @Value("${user.subscription.queue:#{null}}")
    private String userSubscriptionQueue;

    RestTemplate restTemplate;

    ObjectContext objectContext;

    Gson gson;

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
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
    @CrossOrigin
    @ApiOperation(value = "Buy certificate")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_CERT_REQ, method = {RequestMethod.POST})
    public Boolean buyCertificateRequest(BuyCertificateRequest buyCertificateRequest) throws MicroServiceException {
        return null;
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Buy subscription confirm")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_SUB_CONFIRM, method = {RequestMethod.GET})
    public SecureResponse buySubscriptionConfirm(@PathVariable String MD, @PathVariable String PaRes) throws MicroServiceException {
        try {
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
                    .PaRes(PaRes)
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
                        .subscriptionDate(LocalDateTime.now().plusMonths(subscription.getMonths())).build());

                try {
                    RecurrentPayRequest reccurentPayRequest = RecurrentPayRequest.builder()
                            .accountId(userInfo.getUserId().toString())
                            .amount(subscription.getPrice())
                            .currency("RUB")
                            .description("Подписка Thelak на " + subscription.getMonths() + " месяцев.")
                            .email(userInfo.getUserEmail())
                            .interval("Month")
                            .period(subscription.getMonths())
                            .startDate(ZonedDateTime.now().plusMonths(subscription.getMonths()))
                            .requireConfirmation(false)
                            .token(secureResponse.getModel().getToken())
                            .build();

                    DbPaymentsRecurrent dbPaymentsRecurrent = objectContext.newObject(DbPaymentsRecurrent.class);
                    dbPaymentsRecurrent.setIdUser(userInfo.getUserId());
                    dbPaymentsRecurrent.setAmount(reccurentPayRequest.getAmount());
                    dbPaymentsRecurrent.setCurrency(reccurentPayRequest.getCurrency());
                    dbPaymentsRecurrent.setDescription(reccurentPayRequest.getDescription());
                    dbPaymentsRecurrent.setEmail(reccurentPayRequest.getEmail());
                    dbPaymentsRecurrent.setInterval(reccurentPayRequest.getInterval());
                    dbPaymentsRecurrent.setPeriod(reccurentPayRequest.getPeriod());
                    dbPaymentsRecurrent.setStartDate(reccurentPayRequest.getStartDate().toLocalDateTime());
                    dbPaymentsRecurrent.setRequireConfirmation(reccurentPayRequest.getRequireConfirmation());
                    dbPaymentsRecurrent.setToken(reccurentPayRequest.getToken());
                    dbPaymentsRecurrent.setStatus(false);
                    dbPaymentsRecurrent.setCreatedDate(LocalDateTime.now());
                    objectContext.commitChanges();

                    dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                            .where(DbPaymentConfig.NAME.eq("RECURRENT_URL")).selectFirst(objectContext);

                    responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), reccurentPayRequest, String.class);
                    RecurrentPayResponse recurrentPayResponse = gson.fromJson(responseEntity.getBody(), RecurrentPayResponse.class);
                    if (recurrentPayResponse.getSuccess()) {
                        dbPaymentsRecurrent.setStatus(true);
                        dbPaymentsRecurrent.setIdRecurrent(recurrentPayResponse.getModel().getId());
                        objectContext.commitChanges();
                    }
                } catch (Exception ignored) {
                }
            }
            return gson.fromJson(responseEntity.getBody(), SecureResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Redirect before confirm")
    @RequestMapping(value = PAYMENTS_REDIRECT, method = {RequestMethod.POST})
    public ModelAndView redirectBeforeConfirm(@RequestParam String MD, @RequestParam String PaRes) throws MicroServiceException {
        RedirectView rv = new RedirectView();
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        rv.setUrl("https://dev.thelak.com/pay/confirm/" + MD + "/" + PaRes);
        return new ModelAndView(rv);
    }

    @Override
    @CrossOrigin
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
            UserInfo userInfo = null;
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
    @CrossOrigin
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
    @CrossOrigin
    @ApiOperation(value = "Update card info")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_SUB_CANCEL, method = {RequestMethod.GET})
    public Boolean cancelSubscription() throws MicroServiceException {
        UserInfo userInfo = null;
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
    @CrossOrigin
    @ApiOperation(value = "Get cloudpayments public_id")
    @RequestMapping(value = PAYMENTS_CONFIG, method = {RequestMethod.GET})
    public PaymentsConfigModel getConfig() throws MicroServiceException {
        DbPaymentConfig dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                .where(DbPaymentConfig.NAME.eq("PUBLIC_ID")).selectFirst(objectContext);

        return PaymentsConfigModel.builder()
                .name(dbPaymentConfig.getName())
                .value(dbPaymentConfig.getValue())
                .build();
    }
}
