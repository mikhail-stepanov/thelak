package com.thelak.route.video.models;

import com.thelak.route.category.models.CategoryModel;
import com.thelak.route.speaker.models.SpeakerModel;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoCreateRequest {

    String title;

    String description;

    Integer year;

    Integer rating;

    Long viewsCount;

    String country;

    String language;

    List<CategoryModel> category;

    Integer duration;

    List<SpeakerModel> speaker;

    String playground;

    List<VideoSourceModel> sources;

    String partnerLogoUrl;

    String coverUrl;

    String posterUrl;

    Boolean subscription;
}
