package com.thelak.route.video.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoSourceModel {

    String src;

    String type = "video/mp4";

    String label;

    Integer res;
}
