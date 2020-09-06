package com.thelak.route.category.models;

import com.thelak.route.article.models.ArticleModel;
import com.thelak.route.video.models.VideoModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {

    Long id;

    String title;

    String imageUrl;

    List<VideoModel> videos;

    List<ArticleModel> articles;
}
