package com.thelak.route.article.interfaces;

import com.thelak.route.exceptions.MicroServiceException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface IArticleFunctionsService {

    String ARTICLE_RATING_ADD = "/v1/article/rating/add";
    String ARTICLE_RATING_DELETE = "/v1/article/rating/delete";
    String ARTICLE_RATING_CHECK = "/v1/article/rating/check";

    String ARTICLE_STAT_VIEWS = "/v1/article/stat/views";
    String ARTICLE_STAT_LAST = "/v1/article/stat/last";

    Boolean addRating(Long articleId, Integer rating) throws MicroServiceException;

    Boolean deleteRating(Long articleId) throws MicroServiceException;

    Boolean checkRating(Long articleId) throws MicroServiceException;

    HashMap<String, Integer> getViewCount(List<Long> ids) throws MicroServiceException;

    HashMap<String, LocalDateTime> getLastView(List<Long> ids) throws MicroServiceException;
}
