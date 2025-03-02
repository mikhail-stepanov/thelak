package com.thelak.database.entity.auto;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.thelak.database.entity.DbCategoryArticles;
import com.thelak.database.entity.DbCategoryEvents;
import com.thelak.database.entity.DbCategoryVideos;

/**
 * Class _DbCategory was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _DbCategory extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> COVER_URL = Property.create("coverUrl", String.class);
    public static final Property<LocalDateTime> CREATED_DATE = Property.create("createdDate", LocalDateTime.class);
    public static final Property<LocalDateTime> DELETED_DATE = Property.create("deletedDate", LocalDateTime.class);
    public static final Property<String> DESCRIPTION = Property.create("description", String.class);
    public static final Property<LocalDateTime> MODIFIED_DATE = Property.create("modifiedDate", LocalDateTime.class);
    public static final Property<String> TITLE = Property.create("title", String.class);
    public static final Property<List<DbCategoryArticles>> CATEGORY_TO_ARTICLES = Property.create("categoryToArticles", List.class);
    public static final Property<List<DbCategoryEvents>> CATEGORY_TO_EVENTS = Property.create("categoryToEvents", List.class);
    public static final Property<List<DbCategoryVideos>> CATEGORY_TO_VIDEO = Property.create("categoryToVideo", List.class);

    public void setCoverUrl(String coverUrl) {
        writeProperty("coverUrl", coverUrl);
    }
    public String getCoverUrl() {
        return (String)readProperty("coverUrl");
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        writeProperty("createdDate", createdDate);
    }
    public LocalDateTime getCreatedDate() {
        return (LocalDateTime)readProperty("createdDate");
    }

    public void setDeletedDate(LocalDateTime deletedDate) {
        writeProperty("deletedDate", deletedDate);
    }
    public LocalDateTime getDeletedDate() {
        return (LocalDateTime)readProperty("deletedDate");
    }

    public void setDescription(String description) {
        writeProperty("description", description);
    }
    public String getDescription() {
        return (String)readProperty("description");
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        writeProperty("modifiedDate", modifiedDate);
    }
    public LocalDateTime getModifiedDate() {
        return (LocalDateTime)readProperty("modifiedDate");
    }

    public void setTitle(String title) {
        writeProperty("title", title);
    }
    public String getTitle() {
        return (String)readProperty("title");
    }

    public void addToCategoryToArticles(DbCategoryArticles obj) {
        addToManyTarget("categoryToArticles", obj, true);
    }
    public void removeFromCategoryToArticles(DbCategoryArticles obj) {
        removeToManyTarget("categoryToArticles", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DbCategoryArticles> getCategoryToArticles() {
        return (List<DbCategoryArticles>)readProperty("categoryToArticles");
    }


    public void addToCategoryToEvents(DbCategoryEvents obj) {
        addToManyTarget("categoryToEvents", obj, true);
    }
    public void removeFromCategoryToEvents(DbCategoryEvents obj) {
        removeToManyTarget("categoryToEvents", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DbCategoryEvents> getCategoryToEvents() {
        return (List<DbCategoryEvents>)readProperty("categoryToEvents");
    }


    public void addToCategoryToVideo(DbCategoryVideos obj) {
        addToManyTarget("categoryToVideo", obj, true);
    }
    public void removeFromCategoryToVideo(DbCategoryVideos obj) {
        removeToManyTarget("categoryToVideo", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DbCategoryVideos> getCategoryToVideo() {
        return (List<DbCategoryVideos>)readProperty("categoryToVideo");
    }


}
