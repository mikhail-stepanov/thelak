package com.thelak.route.category.interfaces;

import com.thelak.route.category.models.CategoryCreateModel;
import com.thelak.route.category.models.CategoryModel;
import com.thelak.route.exceptions.MicroServiceException;

import java.util.List;

public interface ICategoryService {

    String CATEGORY_GET = "/v1/category/get";
    String CATEGORY_GET_VIDEO = "/v1/category/get/video";
    String CATEGORY_GET_ARTICLE = "/v1/category/get/article";
    String CATEGORY_GET_EVENT = "/v1/category/get/event";
    String CATEGORY_LIST = "/v1/category/list";
    String CATEGORY_CREATE = "/v1/category/create";
    String CATEGORY_UPDATE = "/v1/category/update";
    String CATEGORY_DELETE = "/v1/category/delete";
    String CATEGORY_SEARCH = "/v1/category/search";

    CategoryModel get(Long id) throws MicroServiceException;

    CategoryModel getByVideo(Long videoId) throws MicroServiceException;

    CategoryModel getByArticle(Long articleId) throws MicroServiceException;

    CategoryModel getByEvent(Long eventId) throws MicroServiceException;

    List<CategoryModel> list() throws MicroServiceException;

    List<CategoryModel> search(String search) throws MicroServiceException;

    CategoryModel create(CategoryCreateModel request) throws MicroServiceException;

    CategoryModel update(CategoryModel request) throws MicroServiceException;

    Boolean delete(Long id) throws MicroServiceException;

}
