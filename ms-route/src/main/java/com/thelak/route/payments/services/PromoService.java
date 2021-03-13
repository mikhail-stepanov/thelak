package com.thelak.route.payments.services;

import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.payments.interfaces.IPromoService;
import com.thelak.route.payments.models.promo.PromoCodeCreateRequest;
import com.thelak.route.payments.models.promo.PromoCodeModel;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class PromoService extends BaseMicroservice implements IPromoService {

    public PromoService(RestTemplate restTemplate) {
        super("ms-payments", restTemplate);
    }

    @Override
    public PromoCodeModel get(Long id) throws MicroServiceException {
        return null;
    }

    @Override
    public List<PromoCodeModel> list() throws MicroServiceException {
        return null;
    }

    @Override
    public PromoCodeModel create(PromoCodeCreateRequest request) throws MicroServiceException {
        return null;
    }

    @Override
    public PromoCodeModel update(PromoCodeModel request) throws MicroServiceException {
        return null;
    }

    @Override
    public Boolean delete(Long id) throws MicroServiceException {
        return null;
    }

    @Override
    public PromoCodeModel check(String promo) throws MicroServiceException {
        return null;
    }
}
