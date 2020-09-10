package com.thelak.route.video.models;

import com.thelak.route.category.models.CategoryModel;
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
public class VideoModel {

    Long id;

    String title;

    String description;

    Integer year;

    Integer rating;

    Long viewsCount;

    String country;

    String language;

    CategoryModel category;

    Integer duration;

    String speaker;

    String speakerInformation;

    String playground;

    List<VideoSourceModel> sources;

    String partnerLogoUrl;

    String coverUrl;

    LocalDateTime createdDate;

    LocalDateTime modifiedDate;

    LocalDateTime deletedDate;

}