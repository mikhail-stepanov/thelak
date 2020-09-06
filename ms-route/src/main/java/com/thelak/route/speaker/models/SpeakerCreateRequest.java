package com.thelak.route.speaker.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakerCreateRequest {

    String name;

    String shortDescription;

    String description;

    String country;

    String photoUrl;
}
