package com.thelak.video.service;

import com.thelak.database.entity.DbVideo;
import com.thelak.database.entity.DbVideoRating;
import com.thelak.route.video.models.VideoSourceModel;
import io.swagger.models.auth.In;

import java.util.ArrayList;
import java.util.List;

public class VideoHelper {

    public static List<VideoSourceModel> createSources(DbVideo dbVideo) {
        List<VideoSourceModel> sourceModels = new ArrayList<>();
        sourceModels.add(VideoSourceModel.builder()
                .src(dbVideo.getContentUrl360())
                .label("360p")
                .res(360).build());
        sourceModels.add(VideoSourceModel.builder()
                .src(dbVideo.getContentUrl480())
                .label("480p")
                .res(480).build());
        sourceModels.add(VideoSourceModel.builder()
                .src(dbVideo.getContentUrl720())
                .label("HD")
                .res(720).build());
        sourceModels.add(VideoSourceModel.builder()
                .src(dbVideo.getContentUrl720())
                .label("Full HD")
                .res(720).build());
        return sourceModels;
    }

    public static Integer avgRating(DbVideo dbVideo) {
        int sum = 0;
        List<DbVideoRating> ratings = dbVideo.getVideoToRating();
        if (ratings.size() == 0) return 0;
        for (DbVideoRating rating : ratings) {
            sum = sum + rating.getScore();
        }
        return sum / ratings.size();

    }
}
