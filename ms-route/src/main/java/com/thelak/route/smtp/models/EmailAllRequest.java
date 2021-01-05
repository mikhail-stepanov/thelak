package com.thelak.route.smtp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailAllRequest {

    List<String> to;

    Boolean news;

    Boolean recommendation;

    String subject;

    String htmlBody;

}
