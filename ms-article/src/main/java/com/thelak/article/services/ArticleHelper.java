package com.thelak.article.services;

import com.thelak.database.entity.DbArticle;
import com.thelak.route.article.models.ArticleModel;

public class ArticleHelper {

    public static ArticleModel buildArticleModel(DbArticle dbArticle) {
        return ArticleModel.builder()
                .id((Long) dbArticle.getObjectId().getIdSnapshot().get("id"))
                .title(dbArticle.getTitle())
                .author(dbArticle.getAuthor())
                .description(dbArticle.getDescription())
                .content(dbArticle.getContent())
                .sourceUrl(dbArticle.getSourceUrl())
                .coverUrl(dbArticle.getCoverUrl())
                .createdDate(dbArticle.getCreatedDate())
                .build();
    }
}
