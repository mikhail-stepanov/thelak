package com.thelak.category.endpoints;

import com.thelak.core.endpoints.MicroserviceAdvice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbCategory;
import com.thelak.database.entity.DbCategoryArticles;
import com.thelak.database.entity.DbCategoryEvents;
import com.thelak.database.entity.DbCategoryVideos;
import com.thelak.route.category.interfaces.ICategoryContentService;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "Category content API", produces = "application/json")
public class CategoryContentEndpoint extends MicroserviceAdvice implements ICategoryContentService {

    @Autowired
    private DatabaseService databaseService;

    @Override
    @ApiOperation(value = "Get video ids by category ids")
    @RequestMapping(value = CATEGORY_VIDEO, method = {RequestMethod.GET})
    public List<Long> videoIds(@RequestParam List<Long> categoryIds) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            List<DbCategory> dbCategories = ObjectSelect.query(DbCategory.class).
                    where(ExpressionFactory.inDbExp(DbCategory.ID_PK_COLUMN, categoryIds))
                    .select(objectContext);

            return ObjectSelect.columnQuery(DbCategoryVideos.class, DbCategoryVideos.ID_VIDEO)
                    .where(DbCategoryVideos.VIDEO_TO_CATEGORY.in(dbCategories)).select(objectContext);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get article ids by category ids")
    @RequestMapping(value = CATEGORY_ARTICLE, method = {RequestMethod.GET})
    public List<Long> articleIds(List<Long> categoryIds) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            List<DbCategory> dbCategories = ObjectSelect.query(DbCategory.class).
                    where(ExpressionFactory.inDbExp(DbCategory.ID_PK_COLUMN, categoryIds))
                    .select(objectContext);

            return ObjectSelect.columnQuery(DbCategoryArticles.class, DbCategoryArticles.ID_ARTICLE)
                    .where(DbCategoryArticles.ARTICLE_TO_CATEGORY.in(dbCategories)).select(objectContext);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get event ids by category ids")
    @RequestMapping(value = CATEGORY_EVENT, method = {RequestMethod.GET})
    public List<Long> eventIds(List<Long> categoryIds) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            List<DbCategory> dbCategories = ObjectSelect.query(DbCategory.class).
                    where(ExpressionFactory.inDbExp(DbCategory.ID_PK_COLUMN, categoryIds))
                    .select(objectContext);

            return ObjectSelect.columnQuery(DbCategoryEvents.class, DbCategoryEvents.ID_EVENT)
                    .where(DbCategoryEvents.EVENT_TO_CATEGORY.in(dbCategories)).select(objectContext);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Add category to video")
    @RequestMapping(value = CATEGORY_VIDEO_ADD, method = {RequestMethod.GET})
    public Boolean videoToCategoryAdd(Long videoId, Long categoryId) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbCategory dbCategory = SelectById.query(DbCategory.class, categoryId)
                    .selectFirst(objectContext);

            DbCategoryVideos dbCategoryVideos = objectContext.newObject(DbCategoryVideos.class);
            dbCategoryVideos.setIdVideo(videoId);
            dbCategoryVideos.setVideoToCategory(dbCategory);

            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Add category to article")
    @RequestMapping(value = CATEGORY_ARTICLE_ADD, method = {RequestMethod.GET})
    public Boolean articleToCategoryAdd(Long articleId, Long categoryId) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbCategory dbCategory = SelectById.query(DbCategory.class, categoryId)
                    .selectFirst(objectContext);

            DbCategoryArticles dbCategoryArticles = objectContext.newObject(DbCategoryArticles.class);
            dbCategoryArticles.setIdArticle(articleId);
            dbCategoryArticles.setArticleToCategory(dbCategory);

            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Add category to event")
    @RequestMapping(value = CATEGORY_EVENT_ADD, method = {RequestMethod.GET})
    public Boolean eventToCategoryAdd(Long eventId, Long categoryId) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbCategory dbCategory = SelectById.query(DbCategory.class, categoryId)
                    .selectFirst(objectContext);

            DbCategoryEvents dbCategoryEvents = objectContext.newObject(DbCategoryEvents.class);
            dbCategoryEvents.setIdEvent(eventId);
            dbCategoryEvents.setEventToCategory(dbCategory);

            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @RequestMapping(value = CATEGORY_VIDEO_DELETE, method = {RequestMethod.GET})
    public Boolean videoToCategoryDelete(Long videoId) throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();
        List<DbCategoryVideos> dbCategoryVideos = ObjectSelect.query(DbCategoryVideos.class)
                .where(DbCategoryVideos.ID_VIDEO.eq(videoId))
                .select(objectContext);
        objectContext.deleteObjects(dbCategoryVideos);
        objectContext.commitChanges();
        return true;
    }

    @Override
    @RequestMapping(value = CATEGORY_ARTICLE_DELETE, method = {RequestMethod.GET})
    public Boolean articleToCategoryDelete(Long articleId) throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();
        List<DbCategoryArticles> dbCategoryArticles = ObjectSelect.query(DbCategoryArticles.class)
                .where(DbCategoryArticles.ID_ARTICLE.eq(articleId))
                .select(objectContext);
        objectContext.deleteObjects(dbCategoryArticles);
        objectContext.commitChanges();
        return true;
    }

    @Override
    @RequestMapping(value = CATEGORY_EVENT_DELETE, method = {RequestMethod.GET})
    public Boolean eventToCategory(Long eventId) throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();
        List<DbCategoryEvents> dbCategoryEvents = ObjectSelect.query(DbCategoryEvents.class)
                .where(DbCategoryEvents.ID_EVENT.eq(eventId))
                .select(objectContext);
        objectContext.deleteObjects(dbCategoryEvents);
        objectContext.commitChanges();
        return true;
    }
}
