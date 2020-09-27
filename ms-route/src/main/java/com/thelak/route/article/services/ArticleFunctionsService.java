package com.thelak.route.article.services;

import com.thelak.route.article.interfaces.IArticleFunctionsService;
import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import org.springframework.web.client.RestTemplate;

public class ArticleFunctionsService extends BaseMicroservice implements IArticleFunctionsService {

    public ArticleFunctionsService(RestTemplate restTemplate) {
        super("ms-article", restTemplate);
    }

    @Override
    public Boolean addRating(Long articleId, Integer rating) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(ARTICLE_RATING_ADD), articleId, Boolean.class, rating).getBody());
    }

    @Override
    public Boolean deleteRating(Long videoId) throws MicroServiceException {
        return false;
    }

    @Override
    public Boolean checkRating(Long articleId) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(ARTICLE_RATING_CHECK), Boolean.class, articleId).getBody());
    }
}