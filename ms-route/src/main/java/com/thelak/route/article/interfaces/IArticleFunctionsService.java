package com.thelak.route.article.interfaces;

import com.thelak.route.exceptions.MicroServiceException;

public interface IArticleFunctionsService {

    String ARTICLE_RATING_ADD = "/v1/article/rating/add";
    String ARTICLE_RATING_DELETE = "/v1/article/rating/delete";
    String ARTICLE_RATING_CHECK = "/v1/article/rating/check";

    Boolean addRating(Long articleId, Integer rating) throws MicroServiceException;

    Boolean deleteRating(Long articleId) throws MicroServiceException;

    Boolean checkRating(Long articleId) throws MicroServiceException;
}
