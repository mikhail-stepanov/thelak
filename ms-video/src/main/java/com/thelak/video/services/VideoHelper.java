package com.thelak.video.services;

import com.thelak.core.models.UserInfo;
import com.thelak.database.entity.DbVideo;
import com.thelak.database.entity.DbVideoRating;
import com.thelak.database.entity.DbVideoViews;
import com.thelak.route.category.models.CategoryModel;
import com.thelak.route.speaker.models.SpeakerModel;
import com.thelak.route.video.models.VideoModel;
import com.thelak.route.video.models.VideoSourceModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VideoHelper {

    public static List<VideoSourceModel> createSources(DbVideo dbVideo, UserInfo userInfo) {
        if (userInfo != null && userInfo.isSubscribe()) {
            List<VideoSourceModel> sourceModels = new ArrayList<>();
            sourceModels.add(VideoSourceModel.builder()
                    .src(dbVideo.getContentUrl360())
                    .type("video/mp4")
                    .label("360p")
                    .res(360).build());
            sourceModels.add(VideoSourceModel.builder()
                    .src(dbVideo.getContentUrl480())
                    .type("video/mp4")
                    .label("480p")
                    .res(480).build());
            sourceModels.add(VideoSourceModel.builder()
                    .src(dbVideo.getContentUrl720())
                    .type("video/mp4")
                    .label("720p")
                    .res(720).build());
            sourceModels.add(VideoSourceModel.builder()
                    .src(dbVideo.getContentUrl720())
                    .type("video/mp4")
                    .label("1080p")
                    .res(1080).build());
            return sourceModels;
        } else return null;
    }

    public static Integer avgRating(DbVideo dbVideo) {
        int sum = 0;
        List<DbVideoRating> ratings = dbVideo.getVideoToRating();
        if (ratings == null || ratings.size() == 0) return 0;
        for (DbVideoRating rating : ratings) {
            sum = sum + rating.getScore();
        }
        return sum / ratings.size();
    }

    public static Long countView(DbVideo dbVideo) {
        try {
            List<DbVideoViews> views = dbVideo.getVideoToView();
            return (long) views.size();
        } catch (Exception e) {
            return 0L;
        }
    }

    public static class SortByDate implements Comparator<VideoModel> {
        @Override
        public int compare(VideoModel a, VideoModel b) {
            return a.getCreatedDate().compareTo(b.getCreatedDate());
        }
    }

    public static VideoModel buildVideoModel(DbVideo dbVideo, CategoryModel categoryModel, SpeakerModel speakerModel, UserInfo userInfo) {
        return VideoModel.builder()
                .id((Long) dbVideo.getObjectId().getIdSnapshot().get("id"))
                .title(dbVideo.getTitle())
                .description(dbVideo.getDescription())
                .year(dbVideo.getYear())
                .country(dbVideo.getCountry())
                .language(dbVideo.getLanguage())
                .category(categoryModel)
                .duration(dbVideo.getDuration())
                .speaker(speakerModel)
                .playground(dbVideo.getPlayground())
                .sources(createSources(dbVideo, userInfo))
                .rating(dbVideo.getRating())
                .viewsCount(countView(dbVideo))
                .partnerLogoUrl(dbVideo.getPartnerLogoUrl())
                .coverUrl(dbVideo.getCoverUrl())
                .posterUrl(dbVideo.getPosterUrl())
                .createdDate(dbVideo.getCreatedDate())
                .build();
    }
}
