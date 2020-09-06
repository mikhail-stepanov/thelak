package com.thelak.article.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbArticle;
import com.thelak.database.entity.DbVideo;
import com.thelak.route.article.interfaces.IArticleService;
import com.thelak.route.article.models.ArticleCreateModel;
import com.thelak.route.article.models.ArticleModel;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.thelak.article.services.ArticleHelper.buildArticleModel;

@RestController
@Api(value = "Article API", produces = "application/json")
public class ArticleEndpoint extends AbstractMicroservice implements IArticleService {

    @Autowired
    private DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(ArticleEndpoint.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get article by id")
    @RequestMapping(value = ARTICLE_GET, method = {RequestMethod.GET})
    public ArticleModel get(@RequestParam Long id) throws MicroServiceException {
        try {
            DbArticle dbArticle = SelectById.query(DbArticle.class, id).selectFirst(objectContext);

            return buildArticleModel(dbArticle);

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while get article");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get list of articles by ids")
    @RequestMapping(value = ARTICLE_GET_IDS, method = {RequestMethod.GET})
    public List<ArticleModel> getByIds(@RequestParam List<Long> ids) throws MicroServiceException {
        try {
            List<DbArticle> dbArticles;
            dbArticles = ObjectSelect.query(DbArticle.class).
                    where(ExpressionFactory.inDbExp(DbVideo.ID_PK_COLUMN, ids))
                    .select(objectContext);

            List<ArticleModel> articleModels = new ArrayList<>();

            dbArticles.forEach(dbArticle -> {
                articleModels.add(buildArticleModel(dbArticle));
            });

            return articleModels;
        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while getting articles by ids");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get list of speakers")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "page",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "size",
                    paramType = "query")})
    @RequestMapping(value = ARTICLE_LIST, method = {RequestMethod.GET})
    public List<ArticleModel> list(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer size) throws MicroServiceException {
        try {

            List<DbArticle> dbArticles;
            if (page == null || size == null)
                dbArticles = ObjectSelect.query(DbArticle.class)
                        .pageSize(30)
                        .select(objectContext);
            else {
                dbArticles = ObjectSelect.query(DbArticle.class)
                        .pageSize(size)
                        .select(objectContext);
                dbArticles = dbArticles.subList(page * size - size, page * size);
            }

            List<ArticleModel> articleModels = new ArrayList<>();

            dbArticles.forEach(dbArticle -> {
                articleModels.add(buildArticleModel(dbArticle));
            });

            return articleModels;
        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while get list of Articles");
        }
    }

    @Override
    @CrossOrigin
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

            List<DbArticle> dbArticles;
            if (page == null || size == null)
                dbArticles = ObjectSelect.query(DbArticle.class).
                        where(DbArticle.DELETED_DATE.isNull())
                        .and(DbArticle.DESCRIPTION.containsIgnoreCase(search.toLowerCase()))
                        .or(DbArticle.TITLE.containsIgnoreCase(search.toLowerCase()))
                        .or(DbArticle.AUTHOR.containsIgnoreCase(search.toLowerCase()))
                        .pageSize(30)
                        .select(objectContext);
            else {
                dbArticles = ObjectSelect.query(DbArticle.class).
                        where(DbArticle.DELETED_DATE.isNull())
                        .and(DbArticle.DESCRIPTION.containsIgnoreCase(search.toLowerCase()))
                        .or(DbArticle.TITLE.containsIgnoreCase(search.toLowerCase()))
                        .or(DbArticle.AUTHOR.containsIgnoreCase(search.toLowerCase()))
                        .pageSize(size)
                        .select(objectContext);
                dbArticles = dbArticles.subList(page * size - size, page * size);
            }

            List<ArticleModel> articleModels = new ArrayList<>();

            dbArticles.forEach(dbArticle -> {
                articleModels.add(buildArticleModel(dbArticle));
            });

            return articleModels;
        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while searching articles");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Create article")
    @RequestMapping(value = ARTICLE_CREATE, method = {RequestMethod.POST})
    public ArticleModel create(@RequestBody ArticleCreateModel request) throws MicroServiceException {
        try {

            DbArticle dbArticle = objectContext.newObject(DbArticle.class);
            dbArticle.setTitle(request.getTitle());
            dbArticle.setDescription(request.getDescription());
            dbArticle.setAuthor(request.getAuthor());
            dbArticle.setContent(request.getContent());
            dbArticle.setSourceUrl(request.getSourceUrl());
            dbArticle.setCreatedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return buildArticleModel(dbArticle);

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while create article: " + e.getLocalizedMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Update article by id")
    @RequestMapping(value = ARTICLE_UPDATE, method = {RequestMethod.PUT})
    public ArticleModel update(@RequestBody ArticleModel request) throws MicroServiceException {
        try {

            DbArticle dbArticle = SelectById.query(DbArticle.class, request.getId()).selectFirst(objectContext);

            dbArticle.setTitle(request.getTitle());
            dbArticle.setDescription(request.getDescription());
            dbArticle.setAuthor(request.getAuthor());
            dbArticle.setContent(request.getContent());
            dbArticle.setSourceUrl(request.getSourceUrl());
            dbArticle.setModifiedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return buildArticleModel(dbArticle);

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while updating article");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Delete article by id")
    @RequestMapping(value = ARTICLE_DELETE, method = {RequestMethod.DELETE})
    public Boolean delete(@RequestParam Long id) throws MicroServiceException {
        try {
            DbArticle dbArticle = SelectById.query(DbArticle.class, id).selectFirst(objectContext);

            dbArticle.setDeletedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while deleting article");
        }
    }
}
