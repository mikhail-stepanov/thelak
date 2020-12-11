package com.thelak.payments.services;

import com.thelak.database.entity.*;
import com.thelak.route.payments.models.certificate.CertificateModel;
import com.thelak.route.payments.models.promo.PromoCodeModel;
import com.thelak.route.payments.models.subscription.SubscriptionModel;

import java.util.ArrayList;
import java.util.List;

public class PaymentsHelper {

    public static SubscriptionModel buildSubscriptionModel(DbSubscription dbSubscription) {
        List<String> list = new ArrayList<>();
        List<DbOptionSubscription> dbOptionSubscriptions = dbSubscription.getSubscriptionToOpsub();
        dbOptionSubscriptions.forEach(dbOptionSubscription -> {
            list.add(dbOptionSubscription.getOpsubToOptions().getText());
        });
        return SubscriptionModel.builder()
                .id((Long) dbSubscription.getObjectId().getIdSnapshot().get("id"))
                .days(dbSubscription.getDays())
                .cover(dbSubscription.getCover())
                .list(list)
                .months(dbSubscription.getMonths())
                .next(dbSubscription.getNext())
                .pre(dbSubscription.getPre())
                .price(dbSubscription.getPrice())
                .type(dbSubscription.getType())
                .build();
    }

    public static CertificateModel buildCertificateModel(DbCertificate dbCertificate) {
        List<String> list = new ArrayList<>();
        List<DbOptionCertificate> certificateToOption = dbCertificate.getCertificateToOption();
        certificateToOption.forEach(dbOptionCertificate -> {
            list.add(dbOptionCertificate.getOpsubToOptions().getText());
        });
        return CertificateModel.builder()
                .id((Long) dbCertificate.getObjectId().getIdSnapshot().get("id"))
                .cover(dbCertificate.getCover())
                .length(dbCertificate.getLength())
                .list(list)
                .months(dbCertificate.getMonths())
                .name(dbCertificate.getName())
                .price(dbCertificate.getPrice())
                .priceStr(dbCertificate.getPriceStr())
                .priceStr2(dbCertificate.getPriceStr2())
                .description(dbCertificate.getDescription())
                .build();
    }

    public static PromoCodeModel buildPromoModel(DbPromo dbPromo){
        List<String> list = new ArrayList<>();
        dbPromo.getPromoToEmail().forEach(dbPromoEmail -> {
            list.add(dbPromoEmail.getEmail());
        });

        return PromoCodeModel.builder()
                .id((Long) dbPromo.getObjectId().getIdSnapshot().get("id"))
                .active(dbPromo.isActive())
                .code(dbPromo.getCode())
                .description(dbPromo.getDescription())
                .months(dbPromo.getMonths())
                .emails(list)
                .build();
    }
}