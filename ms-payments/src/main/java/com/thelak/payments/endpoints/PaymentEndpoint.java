package com.thelak.payments.endpoints;

import com.google.gson.Gson;
import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbPaymentConfig;
import com.thelak.database.entity.DbPaymentsCryptogrammSubscription;
import com.thelak.database.entity.DbSubscription;
import com.thelak.route.auth.interfaces.IAuthenticationService;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.exceptions.MsNotAuthorizedException;
import com.thelak.route.payments.interfaces.IPaymentService;
import com.thelak.route.payments.models.*;
import com.thelak.route.payments.models.cloudpayments.cryptogramm.CryptogrammPayRequest;
import com.thelak.route.payments.models.cloudpayments.cryptogramm.CryptogrammPayResponse;
import com.thelak.route.payments.models.cloudpayments.reccurent.ReccurentPayRequest;
import com.thelak.route.payments.models.cloudpayments.reccurent.ReccurentPayResponse;
import com.thelak.route.payments.models.cloudpayments.secure.ConfirmModel;
import com.thelak.route.payments.models.cloudpayments.secure.DSecureRequest;
import com.thelak.route.payments.models.cloudpayments.secure.DSecureResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@Api(value = "Payment API", produces = "application/json")
public class PaymentEndpoint extends AbstractMicroservice implements IPaymentService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private IAuthenticationService authenticationService;

    RestTemplate restTemplate;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(PaymentEndpoint.class);

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
    public ReccurentPayResponse buySubscriptionConfirm(@PathVariable String MD, @PathVariable String PaRes) throws MicroServiceException {
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

            DbPaymentsCryptogrammSubscription dbPaymentsCryptogrammSubscription = ObjectSelect.query(DbPaymentsCryptogrammSubscription.class)
                    .where(DbPaymentsCryptogrammSubscription.TRANSACTION_ID.eq(Long.valueOf(MD)))
                    .selectFirst(objectContext);

            DbPaymentConfig dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                    .where(DbPaymentConfig.NAME.eq("3D_SECURE_URL")).selectFirst(objectContext);

            DSecureRequest dSecureRequest = DSecureRequest.builder()
                    .PaRes(PaRes)
                    .TransactionId(Long.valueOf(MD))
                    .build();

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), dSecureRequest, String.class);
            Gson gson = new Gson();
            DSecureResponse dSecureResponse = gson.fromJson(responseEntity.getBody(), DSecureResponse.class);

            DbSubscription subscription = dbPaymentsCryptogrammSubscription.getCryptogrammToSubscription();

            ReccurentPayRequest reccurentPayRequest = ReccurentPayRequest.builder()
                    .accountId(userInfo.getUserId().toString())
                    .amount(subscription.getPrice())
                    .currency("RUB")
                    .description("Подписка Thelak на " + subscription.getMonths() + " месяцев.")
                    .email(userInfo.getUserEmail())
                    .interval("Month")
                    .period(subscription.getMonths())
                    .startDate(LocalDateTime.now().plusMonths(subscription.getMonths()))
                    .requireConfirmation(false)
                    .token(dSecureResponse.getModel().getToken())
                    .build();

            dbPaymentConfig = ObjectSelect.query(DbPaymentConfig.class)
                    .where(DbPaymentConfig.NAME.eq("RECCURENT_URL")).selectFirst(objectContext);

            responseEntity = restTemplate.postForEntity(dbPaymentConfig.getValue(), reccurentPayRequest, String.class);

            authenticationService.setSubscription(SetSubscriptionModel.builder()
                    .userId(userInfo.getUserId())
                    .subscriptionDate(LocalDateTime.now().plusMonths(subscription.getMonths())).build());

            return gson.fromJson(responseEntity.getBody(), ReccurentPayResponse.class);
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
        rv.setUrl("https://dev.thelak.com/v1/payments/sub/confirm/" + MD + "/" + PaRes);
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
            Gson gson = new Gson();
            CryptogrammPayResponse result = gson.fromJson(responseEntity.getBody(), CryptogrammPayResponse.class);
            DbPaymentsCryptogrammSubscription dbPaymentsCryptogrammSubscription = objectContext.newObject(DbPaymentsCryptogrammSubscription.class);
            dbPaymentsCryptogrammSubscription.setAmount(cryptogrammPayRequest.getAmount());
            dbPaymentsCryptogrammSubscription.setCardCryptogram(cryptogrammPayRequest.getCardCryptogramPacket());
            dbPaymentsCryptogrammSubscription.setCreatedDate(LocalDateTime.now());
            dbPaymentsCryptogrammSubscription.setCurrency(cryptogrammPayRequest.getCurrency());
            dbPaymentsCryptogrammSubscription.setDescription(cryptogrammPayRequest.getDescription());
            dbPaymentsCryptogrammSubscription.setCryptogrammToSubscription(dbSubscription);
            dbPaymentsCryptogrammSubscription.setIdUser(cryptogrammPayRequest.getAccountId());
            dbPaymentsCryptogrammSubscription.setName(cryptogrammPayRequest.getName());
            dbPaymentsCryptogrammSubscription.setStatus(false);
            dbPaymentsCryptogrammSubscription.setTransactionId(result.getModel().getTransactionId());

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
