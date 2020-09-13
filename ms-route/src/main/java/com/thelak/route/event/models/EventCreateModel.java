package com.thelak.route.event.models;

import com.thelak.route.category.models.CategoryModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateModel {

    String title;

    String description;

    String content;

    String coverUrl;

    LocalDate date;

    List<CategoryModel> categories;

}
