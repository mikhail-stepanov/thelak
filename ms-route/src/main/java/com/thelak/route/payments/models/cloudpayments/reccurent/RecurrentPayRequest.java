package com.thelak.route.payments.models.cloudpayments.reccurent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurrentPayRequest {

    String description;

    Integer amount;

    String currency;

    String token;

    String accountId;

    String email;

    Boolean requireConfirmation;

    String startDate;

    String interval;

    Integer period;
}
