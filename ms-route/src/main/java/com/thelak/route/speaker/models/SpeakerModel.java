package com.thelak.route.speaker.models;

import com.thelak.route.article.models.ArticleModel;
import com.thelak.route.category.models.CategoryModel;
import com.thelak.route.video.models.VideoModel;
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
public class SpeakerModel {

    Long id;

    String name;

    String shortDescription;

    String description;

    String country;

    String countryFlagCode;

    String photoUrl;

    List<CategoryModel> categories;

    List<VideoModel> videos;

    List<ArticleModel> articles;

    LocalDateTime createdDate;

    LocalDateTime modifiedDate;

    LocalDateTime deletedDate;
}
