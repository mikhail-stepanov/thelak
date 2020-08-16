package com.thelak.route.video.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    String contentUrl;

    String partnerLogoUrl;

    String coverUrl;
}
