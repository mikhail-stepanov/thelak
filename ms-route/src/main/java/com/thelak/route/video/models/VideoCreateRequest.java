package com.thelak.route.video.models;

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

    String country;

    String category;

    String duration;

    String speaker;

    String speakerInformation;

    List<VideoSourceModel> sources;

    String partnerLogoUrl;

    String coverUrl;
}
