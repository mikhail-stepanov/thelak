package com.thelak.payments.endpoints;

import com.thelak.core.endpoints.MicroserviceAdvice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbSubscription;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.payments.interfaces.ISubscriptionService;
import com.thelak.route.payments.models.subscription.SubscriptionModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.thelak.payments.services.PaymentsHelper.buildSubscriptionModel;

@RestController
@Api(value = "Subscription API", produces = "application/json")
public class SubscriptionEndpoint extends MicroserviceAdvice implements ISubscriptionService {

    @Autowired
    private DatabaseService databaseService;

    @Override
    @ApiOperation(value = "Get subscription by id")
    @RequestMapping(value = SUBSCRIPTION_GET, method = {RequestMethod.GET})
    public SubscriptionModel get(@RequestParam Long id) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbSubscription dbSubscription = SelectById.query(DbSubscription.class, id).selectFirst(objectContext);

            return buildSubscriptionModel(dbSubscription);
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of subscriptions")
    @RequestMapping(value = SUBSCRIPTION_LIST, method = {RequestMethod.GET})
    public List<SubscriptionModel> list() throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            List<DbSubscription> dbSubscriptions = ObjectSelect.query(DbSubscription.class)
                    .orderBy(DbSubscription.MONTHS.asc())
                    .select(objectContext);

            List<SubscriptionModel> subscriptionModels = new ArrayList<>();

            dbSubscriptions.forEach(dbSubscription -> {
                subscriptionModels.add(buildSubscriptionModel(dbSubscription));
            });

            return subscriptionModels;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }
}
