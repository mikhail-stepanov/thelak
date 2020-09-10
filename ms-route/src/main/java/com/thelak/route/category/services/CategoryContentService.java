package com.thelak.route.category.services;

import com.thelak.route.category.interfaces.ICategoryContentService;
import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class CategoryContentService extends BaseMicroservice implements ICategoryContentService {

    public CategoryContentService(RestTemplate restTemplate) {
        super("ms-category", restTemplate);
    }

    @Override
    public List<Long> videoIds(List<Long> categoryIds) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_VIDEO))
                .queryParam("categoryIds", categoryIds);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), List.class, categoryIds).getBody());
    }

    @Override
    public List<Long> articleIds(List<Long> categoryIds) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_ARTICLE))
                .queryParam("categoryIds", categoryIds);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), List.class, categoryIds).getBody());
    }

    @Override
    public List<Long> eventIds(List<Long> categoryIds) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_EVENT))
                .queryParam("categoryIds", categoryIds);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), List.class, categoryIds).getBody());
    }
}
