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
public class VideoFilterModel {

    List<String> countries;

    List<String> playgrounds;

    List<String> languages;

    List<Integer> years;

}
