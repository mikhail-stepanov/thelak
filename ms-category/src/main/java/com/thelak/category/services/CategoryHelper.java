package com.thelak.category.services;

import com.thelak.database.entity.DbCategory;
import com.thelak.route.category.models.CategoryModel;

public class CategoryHelper {

    public static CategoryModel buildCategoryModel(DbCategory dbCategory) {
        return CategoryModel.builder()
                .id((Long) dbCategory.getObjectId().getIdSnapshot().get("id"))
                .title(dbCategory.getTitle())
                .imageUrl(dbCategory.getCoverUrl())
                .build();
    }

}
