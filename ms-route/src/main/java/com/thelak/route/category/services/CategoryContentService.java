package com.thelak.route.category.services;

import com.thelak.route.category.interfaces.ICategoryContentService;
import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class CategoryContentService extends BaseMicroservice implements ICategoryContentService {

    public CategoryContentService(RestTemplate restTemplate) {
        super("ms-category", restTemplate);
    }

    @Override
    public List<Long> videoIds(List<Long> categoryIds) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(CATEGORY_VIDEO), List.class, categoryIds).getBody());
    }

    @Override
    public List<Long> articleIds(List<Long> categoryIds) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(CATEGORY_ARTICLE), List.class, categoryIds).getBody());
    }

    @Override
    public List<Long> eventIds(List<Long> categoryIds) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(CATEGORY_EVENT), List.class, categoryIds).getBody());
    }
}
