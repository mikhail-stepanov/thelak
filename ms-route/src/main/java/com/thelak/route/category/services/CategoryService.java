package com.thelak.route.category.services;

import com.thelak.route.category.interfaces.ICategoryService;
import com.thelak.route.category.models.CategoryCreateModel;
import com.thelak.route.category.models.CategoryModel;
import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class CategoryService extends BaseMicroservice implements ICategoryService {

    public CategoryService(RestTemplate restTemplate) {
        super("ms-category", restTemplate);
    }

    @Override
    public CategoryModel get(Long id) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_GET))
                .queryParam("id", id);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), CategoryModel.class, id).getBody());
    }

    @Override
    public CategoryModel getByVideo(Long videoId) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_GET_VIDEO))
                .queryParam("videoId", videoId);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), CategoryModel.class, videoId).getBody());
    }

    @Override
    public CategoryModel getByArticle(Long articleId) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_GET_ARTICLE))
                .queryParam("articleId", articleId);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), CategoryModel.class, articleId).getBody());
    }

    @Override
    public CategoryModel getByEvent(Long eventId) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(CATEGORY_GET_EVENT))
                .queryParam("eventId", eventId);

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), CategoryModel.class, eventId).getBody());
    }

    @Override
    public List<CategoryModel> list() throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(CATEGORY_LIST), List.class).getBody());
    }

    @Override
    public List<CategoryModel> search(String search) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(CATEGORY_SEARCH), List.class, search).getBody());
    }

    @Override
    public CategoryModel create(CategoryCreateModel request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(CATEGORY_CREATE), request, CategoryModel.class).getBody());
    }

    @Override
    public CategoryModel update(CategoryModel request) throws MicroServiceException {
        return null;
    }

    @Override
    public Boolean delete(Long id) throws MicroServiceException {
        return false;
    }
}
