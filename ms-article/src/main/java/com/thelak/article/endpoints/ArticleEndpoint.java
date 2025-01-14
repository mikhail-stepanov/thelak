package com.thelak.article.endpoints;

import com.thelak.core.endpoints.MicroserviceAdvice;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbArticle;
import com.thelak.database.entity.DbArticleView;
import com.thelak.database.entity.DbEvent;
import com.thelak.route.article.enums.ArticleSortEnum;
import com.thelak.route.article.enums.ArticleSortTypeEnum;
import com.thelak.route.article.interfaces.IArticleService;
import com.thelak.route.article.models.ArticleCreateModel;
import com.thelak.route.article.models.ArticleModel;
import com.thelak.route.category.interfaces.ICategoryContentService;
import com.thelak.route.category.interfaces.ICategoryService;
import com.thelak.route.category.models.CategoryModel;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static com.thelak.article.services.ArticleHelper.buildArticleModel;
import static com.thelak.article.services.ArticleHelper.countView;

@RestController
@Api(value = "Article API", produces = "application/json")
public class ArticleEndpoint extends MicroserviceAdvice implements IArticleService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ICategoryContentService categoryContentService;

    @Override
    @ApiOperation(value = "Get article by id")
    @RequestMapping(value = ARTICLE_GET, method = {RequestMethod.GET})
    public ArticleModel get(@RequestParam Long id) throws MicroServiceException {
        try {
            long userId;
            UserInfo userInfo;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
                userId = userInfo.getUserId();
            } catch (Exception e) {
                userId = -1;
            }

            ObjectContext objectContext = databaseService.getContext();

            DbArticle dbArticle = SelectById.query(DbArticle.class, id).selectFirst(objectContext);

            if (dbArticle.getDeletedDate() != null) return null;

            DbArticleView dbArticleView = objectContext.newObject(DbArticleView.class);
            dbArticleView.setCreatedDate(LocalDateTime.now());
            dbArticleView.setIdUser(userId);
            dbArticleView.setViewToArticle(dbArticle);

            objectContext.commitChanges();

            dbArticle.setView(countView(dbArticle));

            objectContext.commitChanges();

            List<CategoryModel> categoryModel = categoryService.getByArticle(id);

            return buildArticleModel(dbArticle, categoryModel, true);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of articles by ids")
    @RequestMapping(value = ARTICLE_GET_IDS, method = {RequestMethod.GET})
    public List<ArticleModel> getByIds(@RequestParam List<Long> ids) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            List<DbArticle> dbArticles;
            dbArticles = ObjectSelect.query(DbArticle.class).
                    where(ExpressionFactory.inDbExp(DbArticle.ID_PK_COLUMN, ids))
                    .and(DbArticle.DELETED_DATE.isNull())
                    .select(objectContext);

            List<ArticleModel> articleModels = new ArrayList<>();

            dbArticles.forEach(dbArticle -> {
                List<CategoryModel> categoryModel = null;
                try {
                    categoryModel = categoryService.getByArticle((Long) dbArticle.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException ignored) {
                }
                articleModels.add(buildArticleModel(dbArticle, categoryModel, false));
            });

            return articleModels;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of articles")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "page",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "size",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "sort",
                    dataType = "com.thelak.route.article.enums.ArticleSortEnum",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "sortType",
                    dataType = "com.thelak.route.article.enums.ArticleSortTypeEnum",
                    paramType = "query")})
    @RequestMapping(value = ARTICLE_LIST, method = {RequestMethod.GET})
    public List<ArticleModel> list(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer size,
                                   @RequestParam(required = false) ArticleSortEnum sort, @RequestParam(required = false) ArticleSortTypeEnum sortType) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            List<DbArticle> dbArticles;
            if (page == null || size == null)
                dbArticles = ObjectSelect.query(DbArticle.class)
                        .where(DbArticle.DELETED_DATE.isNull())
                        .orderBy(DbArticle.CREATED_DATE.desc())
                        .pageSize(30)
                        .select(objectContext);
            else {
                dbArticles = ObjectSelect.query(DbArticle.class)
                        .where(DbArticle.DELETED_DATE.isNull())
                        .orderBy(DbArticle.CREATED_DATE.desc())
                        .pageSize(size)
                        .select(objectContext);
                if (dbArticles.size() >= size * page)
                    dbArticles = dbArticles.subList(page * size - size, page * size);
                else if (dbArticles.size() >= size * (page - 1))
                    dbArticles = dbArticles.subList(page * size - size, dbArticles.size() - 1);
                else
                    dbArticles = new ArrayList<>();
            }

            List<ArticleModel> articleModels = new ArrayList<>();

            dbArticles.forEach(dbArticle -> {
                List<CategoryModel> categoryModel = null;
                try {
                    categoryModel = categoryService.getByArticle((Long) dbArticle.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException ignored) {
                }
                articleModels.add(buildArticleModel(dbArticle, categoryModel, false));
            });

            if (sort != null && !articleModels.isEmpty()) {
                if (sort == ArticleSortEnum.NEW) {
                    articleModels.sort(Comparators.NEW);
                    if (sortType == ArticleSortTypeEnum.DESC)
                        Collections.reverse(articleModels);
                }
                if (sort == ArticleSortEnum.RATING) {
                    articleModels.sort(Comparators.RATING);
                    if (sortType == ArticleSortTypeEnum.DESC)
                        Collections.reverse(articleModels);
                }
                if (sort == ArticleSortEnum.POPULAR) {
                    articleModels.sort(Comparators.POPULAR);
                    if (sortType == ArticleSortTypeEnum.DESC)
                        Collections.reverse(articleModels);
                }
            }
            return articleModels;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Find article by title/author/description")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "page",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "size",
                    paramType = "query")})
    @RequestMapping(value = ARTICLE_SEARCH, method = {RequestMethod.GET})
    public List<ArticleModel> search(@RequestParam String search,
                                     @RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer size) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            List<DbArticle> dbArticles;
            if (page == null || size == null)
                dbArticles = ObjectSelect.query(DbArticle.class).
                        where(DbArticle.DELETED_DATE.isNull())
                        .and(DbArticle.DESCRIPTION.containsIgnoreCase(search.toLowerCase()))
                        .or(DbArticle.TITLE.containsIgnoreCase(search.toLowerCase()))
                        .and(DbArticle.DELETED_DATE.isNull())
                        .or(DbArticle.AUTHOR.containsIgnoreCase(search.toLowerCase()))
                        .and(DbArticle.DELETED_DATE.isNull())
                        .orderBy(DbArticle.CREATED_DATE.desc())
                        .pageSize(30)
                        .select(objectContext);
            else {
                dbArticles = ObjectSelect.query(DbArticle.class).
                        where(DbArticle.DELETED_DATE.isNull())
                        .and(DbArticle.DESCRIPTION.containsIgnoreCase(search.toLowerCase()))
                        .or(DbArticle.TITLE.containsIgnoreCase(search.toLowerCase()))
                        .and(DbArticle.DELETED_DATE.isNull())
                        .or(DbArticle.AUTHOR.containsIgnoreCase(search.toLowerCase()))
                        .and(DbArticle.DELETED_DATE.isNull())
                        .orderBy(DbArticle.CREATED_DATE.desc())
                        .pageSize(size)
                        .select(objectContext);
                if (dbArticles.size() >= size * page)
                    dbArticles = dbArticles.subList(page * size - size, page * size);
                else if (dbArticles.size() >= size * (page - 1))
                    dbArticles = dbArticles.subList(page * size - size, dbArticles.size());
                else
                    dbArticles = new ArrayList<>();
            }

            List<ArticleModel> articleModels = new ArrayList<>();

            dbArticles.forEach(dbArticle -> {
                List<CategoryModel> categoryModel = null;
                try {
                    categoryModel = categoryService.getByArticle((Long) dbArticle.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException ignored) {
                }
                articleModels.add(buildArticleModel(dbArticle, categoryModel, false));
            });

            return articleModels;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Create article")
    @RequestMapping(value = ARTICLE_CREATE, method = {RequestMethod.POST})
    public ArticleModel create(@RequestBody ArticleCreateModel request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbArticle dbArticle = objectContext.newObject(DbArticle.class);
            dbArticle.setTitle(request.getTitle());
            dbArticle.setDescription(request.getDescription());
            dbArticle.setAuthor(request.getAuthor());
            dbArticle.setContent(request.getContent());
            dbArticle.setSourceUrl(request.getSourceUrl());
            dbArticle.setCoverUrl(request.getCoverUrl());
            dbArticle.setRating(0);
            dbArticle.setView(0);
            dbArticle.setCreatedDate(LocalDateTime.now());

            objectContext.commitChanges();

            if (request.getCategories() != null)
                request.getCategories().forEach(categoryModel -> {
                    try {
                        categoryContentService.articleToCategoryAdd((Long) dbArticle.getObjectId().getIdSnapshot().get("id"),
                                categoryModel.getId());
                    } catch (MicroServiceException e) {
                        e.printStackTrace();
                    }
                });

            return buildArticleModel(dbArticle, request.getCategories(), true);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Update article by id")
    @RequestMapping(value = ARTICLE_UPDATE, method = {RequestMethod.PUT})
    public ArticleModel update(@RequestBody ArticleModel request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbArticle dbArticle = SelectById.query(DbArticle.class, request.getId()).selectFirst(objectContext);

            dbArticle.setTitle(Optional.ofNullable(request.getTitle()).orElse(dbArticle.getTitle()));
            dbArticle.setDescription(Optional.ofNullable(request.getDescription()).orElse(dbArticle.getDescription()));
            dbArticle.setAuthor(Optional.ofNullable(request.getAuthor()).orElse(dbArticle.getAuthor()));
            dbArticle.setContent(Optional.ofNullable(request.getContent()).orElse(dbArticle.getContent()));
            dbArticle.setSourceUrl(Optional.ofNullable(request.getSourceUrl()).orElse(dbArticle.getSourceUrl()));
            dbArticle.setCoverUrl(Optional.ofNullable(request.getCoverUrl()).orElse(dbArticle.getCoverUrl()));
            dbArticle.setModifiedDate(LocalDateTime.now());

            objectContext.commitChanges();

            categoryContentService.articleToCategoryDelete((Long) dbArticle.getObjectId().getIdSnapshot().get("id"));
            if (request.getCategories() != null)
                request.getCategories().forEach(categoryModel -> {
                    try {
                        categoryContentService.articleToCategoryAdd((Long) dbArticle.getObjectId().getIdSnapshot().get("id"),
                                categoryModel.getId());
                    } catch (MicroServiceException e) {
                        e.printStackTrace();
                    }
                });

            List<CategoryModel> categoryModel = categoryService.getByArticle(request.getId());

            return buildArticleModel(dbArticle, categoryModel, true);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Delete article by id")
    @RequestMapping(value = ARTICLE_DELETE, method = {RequestMethod.DELETE})
    public Boolean delete(@RequestParam Long id) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbArticle dbArticle = SelectById.query(DbArticle.class, id).selectFirst(objectContext);
            dbArticle.setDeletedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    public static class Comparators {
        public static final Comparator<ArticleModel> POPULAR = Comparator.comparing(ArticleModel::getViewsCount);
        public static final Comparator<ArticleModel> RATING = Comparator.comparing(ArticleModel::getRating);
        public static final Comparator<ArticleModel> NEW = Comparator.comparing(ArticleModel::getCreatedDate);

    }
}
