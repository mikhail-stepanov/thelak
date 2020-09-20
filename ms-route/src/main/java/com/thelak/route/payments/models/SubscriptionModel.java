package com.thelak.route.payments.models;

import com.thelak.route.category.models.CategoryModel;
import com.thelak.route.speaker.models.SpeakerModel;
import com.thelak.route.video.models.VideoSourceModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionModel {
    
    Long id;

    Integer months;

    String pre;

    String next;

    Integer price;

    String type;

    Integer cover;

    Integer days;

    List<String> list;

    LocalDateTime createdDate;

    LocalDateTime modifiedDate;

    LocalDateTime deletedDate;
}
