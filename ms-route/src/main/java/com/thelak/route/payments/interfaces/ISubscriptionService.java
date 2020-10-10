package com.thelak.route.payments.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.models.subscription.SubscriptionModel;

import java.util.List;

public interface ISubscriptionService {

    String SUBSCRIPTION_GET = "/v1/subscription/get";
    String SUBSCRIPTION_LIST = "/v1/subscription/list";

    SubscriptionModel get(Long id) throws MicroServiceException;

    List<SubscriptionModel> list() throws MicroServiceException;
}
