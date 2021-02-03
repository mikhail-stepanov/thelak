package com.thelak.article.endpoints;

import com.thelak.core.endpoints.MicroserviceAdvice;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbArticle;
import com.thelak.database.entity.DbArticleRating;
import com.thelak.database.entity.DbArticleView;
import com.thelak.database.entity.DbVideo;
import com.thelak.route.article.interfaces.IArticleFunctionsService;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsBadRequestException;
import com.thelak.route.exceptions.MsInternalErrorException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static com.thelak.article.services.ArticleHelper.avgRating;

@RestController
@Api(value = "Article functions API", produces = "application/json")
public class ArticleFunctionsEndpoint extends MicroserviceAdvice implements IArticleFunctionsService {

    @Autowired
    private DatabaseService databaseService;

    @Override
    @CrossOrigin
    @ApiOperation(value = "Rate Article")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = ARTICLE_RATING_ADD, method = {RequestMethod.POST})
    public Boolean addRating(@RequestParam Long articleId, @RequestParam Integer score) throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();

        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbArticle article = SelectById.query(DbArticle.class, articleId).selectFirst(objectContext);

        if (!checkIsRate(article, userInfo.getUserId())) {

            DbArticleRating rating = objectContext.newObject(DbArticleRating.class);

            rating.setCreatedDate(LocalDateTime.now());
            rating.setIdUser(userInfo.getUserId());
            rating.setScore(score);
            rating.setRatingToArticle(article);

            objectContext.commitChanges();

            article.setRating(avgRating(article));

            objectContext.commitChanges();

            return true;
        } else
            throw new MsBadRequestException("Always in favorites");
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Delete rating from article")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = ARTICLE_RATING_DELETE, method = {RequestMethod.DELETE})
    public Boolean deleteRating(@RequestParam Long articleId) throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();

        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbArticle article = SelectById.query(DbArticle.class, articleId).selectFirst(objectContext);

        DbArticleRating rating = ObjectSelect.query(DbArticleRating.class)
                .where(DbArticleRating.ID_USER.eq(userInfo.getUserId()))
                .and(DbArticleRating.RATING_TO_ARTICLE.eq(article))
                .selectFirst(objectContext);

        objectContext.deleteObject(rating);

        objectContext.commitChanges();

        article.setRating(avgRating(article));

        objectContext.commitChanges();

        return true;
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Check article is favorite")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = ARTICLE_RATING_CHECK, method = {RequestMethod.GET})
    public Boolean checkRating(@RequestParam Long articleId) throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();

        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbArticle dbArticle = SelectById.query(DbArticle.class, articleId).selectFirst(objectContext);

        return checkIsRate(dbArticle, userInfo.getUserId());
    }

    @ApiOperation(value = "Get Articles view count by userId")

    @RequestMapping(value = ARTICLE_STAT_VIEWS, method = {RequestMethod.GET})
    public HashMap<String, Integer> getViewCount(@RequestParam List<Long> ids) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            HashMap<String, Integer> result = new HashMap<>();
            ids.forEach(id -> {
                List<DbArticleView> dbArticleViews = ObjectSelect.query(DbArticleView.class)
                        .where(DbArticleView.ID_USER.eq(id))
                        .select(objectContext);
                result.put(id.toString(), dbArticleViews.size());
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @ApiOperation(value = "Get Articles last view by userId")
    @RequestMapping(value = ARTICLE_STAT_LAST, method = {RequestMethod.GET})
    public HashMap<String, LocalDateTime> getLastView(@RequestParam List<Long> ids) throws MicroServiceException {

        try {
            ObjectContext objectContext = databaseService.getContext();

            HashMap<String, LocalDateTime> result = new HashMap<>();
            ids.forEach(id -> {
                DbArticleView dbArticleView = null;
                try {
                    dbArticleView = ObjectSelect.query(DbArticleView.class)
                            .where(DbArticleView.ID_USER.eq(id))
                            .orderBy(DbArticleView.CREATED_DATE.desc())
                            .selectFirst(objectContext);
                }catch (Exception ingored){}
                result.put(id.toString(), dbArticleView != null ? dbArticleView.getCreatedDate() : null);
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsInternalErrorException(e.getMessage());
        }
    }


    private boolean checkIsRate(DbArticle article, Long userId) {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbArticleRating rating = ObjectSelect.query(DbArticleRating.class)
                    .where(DbArticleRating.ID_USER.eq(userId))
                    .and(DbArticleRating.RATING_TO_ARTICLE.eq(article))
                    .selectFirst(objectContext);

            return rating != null;
        } catch (Exception e) {
            return false;
        }
    }
}
