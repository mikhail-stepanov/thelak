package com.thelak.route.article.models;

import com.thelak.route.category.models.CategoryModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreateModel {

    String title;

    String author;

    String description;

    String content;

    String sourceUrl;

    List<CategoryModel> categories;

}
