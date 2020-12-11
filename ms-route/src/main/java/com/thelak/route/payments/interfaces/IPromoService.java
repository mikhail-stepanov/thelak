package com.thelak.route.payments.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.models.promo.PromoCodeCreateRequest;
import com.thelak.route.payments.models.promo.PromoCodeModel;

import java.util.List;

public interface IPromoService {

    String PROMO_GET = "/v1/promo/get";
    String PROMO_CREATE = "/v1/promo/create";
    String PROMO_UPDATE = "/v1/promo/update";
    String PROMO_DELETE = "/v1/promo/delete";
    String PROMO_LIST = "/v1/promo/list";

    PromoCodeModel get(Long id) throws MicroServiceException;

    List<PromoCodeModel> list() throws MicroServiceException;

    PromoCodeModel create(PromoCodeCreateRequest request) throws MicroServiceException;

    PromoCodeModel update(PromoCodeModel request) throws MicroServiceException;

    Boolean delete(Long id) throws MicroServiceException;
}