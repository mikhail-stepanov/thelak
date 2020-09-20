package com.thelak.route.article.services;

import com.thelak.route.article.enums.ArticleSortEnum;
import com.thelak.route.article.enums.ArticleSortTypeEnum;
import com.thelak.route.article.interfaces.IArticleService;
import com.thelak.route.article.models.ArticleCreateModel;
import com.thelak.route.article.models.ArticleModel;
import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class ArticleService extends BaseMicroservice implements IArticleService {

    public ArticleService(RestTemplate restTemplate) {
        super("ms-article", restTemplate);
    }

    @Override
    public ArticleModel get(Long id) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(ARTICLE_GET), ArticleModel.class, id).getBody());
    }

    @Override
    public List<ArticleModel> getByIds(List<Long> ids) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(ARTICLE_GET_IDS), List.class, ids).getBody());
    }

    @Override
    public List<ArticleModel> list(Integer page, Integer size, ArticleSortEnum sort, ArticleSortTypeEnum sortType) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(ARTICLE_LIST), List.class,
                page, size).getBody());
    }

    @Override
    public List<ArticleModel> search(String search, Integer page, Integer size) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(ARTICLE_SEARCH), List.class, search, page, size).getBody());
    }

    @Override
    public ArticleModel create(ArticleCreateModel request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(ARTICLE_CREATE), request, ArticleModel.class).getBody());
    }

    @Override
    public ArticleModel update(ArticleModel request) throws MicroServiceException {
        return null;
    }

    @Override
    public Boolean delete(Long id) throws MicroServiceException {
        return false;
    }
}
