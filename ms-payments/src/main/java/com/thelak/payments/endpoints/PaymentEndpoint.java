package com.thelak.payments.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbPaymentConfig;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.interfaces.IPaymentService;
import com.thelak.route.payments.models.BuyCertificateRequest;
import com.thelak.route.payments.models.BuySubscriptionRequest;
import com.thelak.route.payments.models.CardUpdateRequest;
import com.thelak.route.payments.models.PaymentsConfigModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@Api(value = "Payment API", produces = "application/json")
public class PaymentEndpoint extends AbstractMicroservice implements IPaymentService {

    @Autowired
    private DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(PaymentEndpoint.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
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
    @RequestMapping(value = PAYMENTS_CERT, method = {RequestMethod.POST})
    public Boolean buyCertificate(BuyCertificateRequest buyCertificateRequest) throws MicroServiceException {
        return null;
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Buy subscription")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = PAYMENTS_SUB, method = {RequestMethod.POST})
    public Boolean buySubscription(BuySubscriptionRequest buySubscriptionRequest) throws MicroServiceException {
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
