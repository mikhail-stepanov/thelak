package com.thelak.route.category.interfaces;

import com.thelak.route.exceptions.MicroServiceException;

import java.util.List;

public interface ICategoryContentService {

    String CATEGORY_VIDEO = "/v1/category/content/video";
    String CATEGORY_ARTICLE = "/v1/category/content/article";
    String CATEGORY_EVENT = "/v1/category/content/event";

    List<Long> videoIds(List<Long> categoryIds) throws MicroServiceException;

    List<Long> articleIds(List<Long> categoryIds) throws MicroServiceException;

    List<Long> eventIds(List<Long> categoryIds) throws MicroServiceException;

}
