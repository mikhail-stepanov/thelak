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
                .queryParam("categoryIds", categoryIds.toArray());
        return retry(() -> restTemplate.getForEntity(builder.toUriString(), List.class, categoryIds).getBody());
    }

    @Override
    public List<Long> articleIds(List<Long> categoryIds) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_ARTICLE))
                .queryParam("categoryIds", categoryIds.toArray());

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), List.class, categoryIds).getBody());
    }

    @Override
    public List<Long> eventIds(List<Long> categoryIds) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_EVENT))
                .queryParam("categoryIds", categoryIds.toArray());

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), List.class, categoryIds).getBody());
    }

    @Override
    public Boolean videoToCategoryAdd(Long videoId, Long categoryId) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_VIDEO_ADD))
                .queryParam("videoId", videoId)
                .queryParam("categoryId", categoryId);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), Boolean.class, videoId, categoryId).getBody());
    }

    @Override
    public Boolean articleToCategoryAdd(Long articleId, Long categoryId) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_ARTICLE_ADD))
                .queryParam("articleId", articleId)
                .queryParam("categoryId", categoryId);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), Boolean.class, articleId, categoryId).getBody());
    }

    @Override
    public Boolean eventToCategoryAdd(Long eventId, Long categoryId) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_EVENT_ADD))
                .queryParam("eventId", eventId)
                .queryParam("categoryId", categoryId);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), Boolean.class, eventId, categoryId).getBody());
    }

    @Override
    public Boolean videoToCategoryDelete(Long videoId) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_VIDEO_DELETE))
                .queryParam("videoId", videoId);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), Boolean.class, videoId).getBody());
    }

    @Override
    public Boolean articleToCategoryDelete(Long articleId) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_ARTICLE_DELETE))
                .queryParam("articleId", articleId);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), Boolean.class, articleId).getBody());
    }

    @Override
    public Boolean eventToCategory(Long eventId) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_EVENT_DELETE))
                .queryParam("eventId", eventId);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), Boolean.class, eventId).getBody());
    }
}
