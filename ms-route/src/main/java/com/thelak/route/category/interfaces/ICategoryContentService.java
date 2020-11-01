package com.thelak.route.category.interfaces;

import com.thelak.route.exceptions.MicroServiceException;

import java.util.List;

public interface ICategoryContentService {

    String CATEGORY_VIDEO = "/v1/category/content/video";
    String CATEGORY_ARTICLE = "/v1/category/content/article";
    String CATEGORY_EVENT = "/v1/category/content/event";

    String CATEGORY_VIDEO_ADD = "/v1/category/content/video/add";
    String CATEGORY_ARTICLE_ADD = "/v1/category/content/article/add";
    String CATEGORY_EVENT_ADD = "/v1/category/content/event/add";

    String CATEGORY_VIDEO_DELETE = "/v1/category/content/video/delete";
    String CATEGORY_ARTICLE_DELETE = "/v1/category/content/article/delete";
    String CATEGORY_EVENT_DELETE = "/v1/category/content/event/delete";

    List<Long> videoIds(List<Long> categoryIds) throws MicroServiceException;

    List<Long> articleIds(List<Long> categoryIds) throws MicroServiceException;

    List<Long> eventIds(List<Long> categoryIds) throws MicroServiceException;

    Boolean videoToCategoryAdd(Long videoId, Long categoryId) throws MicroServiceException;

    Boolean articleToCategoryAdd(Long articleId, Long categoryId) throws MicroServiceException;

    Boolean eventToCategoryAdd(Long eventId, Long categoryId) throws MicroServiceException;

    Boolean videoToCategoryDelete(Long videoId) throws MicroServiceException;

    Boolean articleToCategoryDelete(Long articleId) throws MicroServiceException;

    Boolean eventToCategory(Long eventId) throws MicroServiceException;

}
