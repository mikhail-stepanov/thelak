package com.thelak.article.services;

import com.thelak.database.entity.DbArticle;
import com.thelak.database.entity.DbArticleRating;
import com.thelak.database.entity.DbArticleView;
import com.thelak.route.article.models.ArticleModel;
import com.thelak.route.category.models.CategoryModel;

import java.util.List;

public class ArticleHelper {

    public static Integer countView(DbArticle dbArticle) {
        try {
            List<DbArticleView> views = dbArticle.getArticleToView();
            return views.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public static Integer avgRating(DbArticle dbArticle) {
        int sum = 0;
        List<DbArticleRating> ratings = dbArticle.getArticleToRating();
        if (ratings == null || ratings.size() == 0) return 0;
        for (DbArticleRating rating : ratings) {
            sum = sum + rating.getScore();
        }
        return sum / ratings.size();
    }

    public static ArticleModel buildArticleModel(DbArticle dbArticle, List<CategoryModel> categoryModel, boolean fullData) {
        return ArticleModel.builder()
                .id((Long) dbArticle.getObjectId().getIdSnapshot().get("id"))
                .title(dbArticle.getTitle())
                .author(fullData?dbArticle.getAuthor():null)
                .rating(dbArticle.getRating())
                .viewsCount(dbArticle.getView())
                .description(fullData ? dbArticle.getDescription() : null)
                .content(fullData ? dbArticle.getContent() : null)
                .categories(categoryModel)
                .sourceUrl(dbArticle.getSourceUrl())
                .coverUrl(dbArticle.getCoverUrl())
                .createdDate(dbArticle.getCreatedDate())
                .build();
    }
}
