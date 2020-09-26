package com.thelak.route.payments.models.cloudpayments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReccurentPayRequest {

    String description;

    Integer amount;

    String currency;

    String token;

    String accountId;

    String email;

    Boolean requireConfirmation;

    LocalDateTime startDate;

    String interval;

    Integer period;
}
