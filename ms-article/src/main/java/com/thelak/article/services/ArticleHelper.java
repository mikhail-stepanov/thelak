package com.thelak.article.services;

import com.thelak.database.entity.DbArticle;
import com.thelak.database.entity.DbArticleRating;
import com.thelak.route.article.models.ArticleModel;

import java.util.List;

public class ArticleHelper {

    public static Integer avgRating(DbArticle dbArticle) {
        int sum = 0;
        List<DbArticleRating> ratings = dbArticle.getArticleToRating();
        if (ratings == null || ratings.size() == 0) return 0;
        for (DbArticleRating rating : ratings) {
            sum = sum + rating.getScore();
        }
        return sum / ratings.size();
    }

    public static ArticleModel buildArticleModel(DbArticle dbArticle) {
        return ArticleModel.builder()
                .id((Long) dbArticle.getObjectId().getIdSnapshot().get("id"))
                .title(dbArticle.getTitle())
                .author(dbArticle.getAuthor())
                .rating(avgRating(dbArticle))
                .description(dbArticle.getDescription())
                .content(dbArticle.getContent())
                .sourceUrl(dbArticle.getSourceUrl())
                .coverUrl(dbArticle.getCoverUrl())
                .createdDate(dbArticle.getCreatedDate())
                .build();
    }
}
