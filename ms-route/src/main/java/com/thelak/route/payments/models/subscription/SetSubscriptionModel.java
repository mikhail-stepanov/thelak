package com.thelak.route.payments.models.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetSubscriptionModel {

    Long userId;

    LocalDateTime subscriptionDate;
}
