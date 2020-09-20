package com.thelak.route.article.models;

import com.thelak.route.category.models.CategoryModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleModel {

    Long id;

    String title;

    String author;

    String description;

    String content;

    String sourceUrl;

    String coverUrl;

    Integer rating;

    List<CategoryModel> categories;

    LocalDateTime createdDate;

    LocalDateTime modifiedDate;

    LocalDateTime deletedDate;
}
