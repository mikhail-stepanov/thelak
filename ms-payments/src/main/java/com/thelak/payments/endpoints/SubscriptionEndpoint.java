package com.thelak.payments.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.thelak.payments.services.PaymentsHelper.buildSubscriptionModel;

@RestController
@Api(value = "Subscription API", produces = "application/json")
public class SubscriptionEndpoint extends AbstractMicroservice implements ISubscriptionService {

    @Autowired
    private DatabaseService databaseService;

    ObjectContext objectContext;

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get subscription by id")
    @RequestMapping(value = SUBSCRIPTION_GET, method = {RequestMethod.GET})
    public SubscriptionModel get(@RequestParam Long id) throws MicroServiceException {
        try {
            DbSubscription dbSubscription = SelectById.query(DbSubscription.class, id).selectFirst(objectContext);

            return buildSubscriptionModel(dbSubscription);
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get list of subscriptions")
    @RequestMapping(value = SUBSCRIPTION_LIST, method = {RequestMethod.GET})
    public List<SubscriptionModel> list() throws MicroServiceException {
        try {
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
