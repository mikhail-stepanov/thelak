package com.thelak.route.article.interfaces;

import com.thelak.route.article.models.ArticleCreateModel;
import com.thelak.route.article.models.ArticleModel;
import com.thelak.route.exceptions.MicroServiceException;

import java.util.List;

public interface IArticleService {

    String ARTICLE_GET = "/v1/article/get";
    String ARTICLE_GET_IDS = "/v1/article/get/ids";
    String ARTICLE_LIST = "/v1/article/list";
    String ARTICLE_CREATE = "/v1/article/create";
    String ARTICLE_UPDATE = "/v1/article/update";
    String ARTICLE_DELETE = "/v1/article/delete";
    String ARTICLE_SEARCH = "/v1/article/search";

    ArticleModel get(Long id) throws MicroServiceException;

    List<ArticleModel> getByIds(List<Long> ids) throws MicroServiceException;

    List<ArticleModel> list(Integer page, Integer size) throws MicroServiceException;

    List<ArticleModel> search(String search, Integer page, Integer size) throws MicroServiceException;

    ArticleModel create(ArticleCreateModel request) throws MicroServiceException;

    ArticleModel update(ArticleModel request) throws MicroServiceException;

    Boolean delete(Long id) throws MicroServiceException;

}
