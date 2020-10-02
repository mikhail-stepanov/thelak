package com.thelak.article.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbArticle;
import com.thelak.database.entity.DbArticleRating;
import com.thelak.route.article.interfaces.IArticleFunctionsService;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsBadRequestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

import static com.thelak.article.services.ArticleHelper.avgRating;

@RestController
@Api(value = "Article functions API", produces = "application/json")
public class ArticleFunctionsEndpoint extends AbstractMicroservice implements IArticleFunctionsService {

    @Autowired
    private DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(ArticleFunctionsEndpoint.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
//    @CrossOrigin
    @ApiOperation(value = "Rate Article")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = ARTICLE_RATING_ADD, method = {RequestMethod.POST})
    public Boolean addRating(@RequestParam Long articleId, @RequestParam Integer score) throws MicroServiceException {
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
//    @CrossOrigin
    @ApiOperation(value = "Delete rating from article")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = ARTICLE_RATING_DELETE, method = {RequestMethod.DELETE})
    public Boolean deleteRating(@RequestParam Long articleId) throws MicroServiceException {
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
//    @CrossOrigin
    @ApiOperation(value = "Check article is favorite")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = ARTICLE_RATING_CHECK, method = {RequestMethod.GET})
    public Boolean checkRating(@RequestParam Long articleId) throws MicroServiceException {
        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbArticle dbArticle = SelectById.query(DbArticle.class, articleId).selectFirst(objectContext);

        return checkIsRate(dbArticle, userInfo.getUserId());
    }


    private boolean checkIsRate(DbArticle article, Long userId) {
        try {
            DbArticleRating rating = ObjectSelect.query(DbArticleRating.class)
                    .where(DbArticleRating.ID_USER.eq(userId))
                    .and(DbArticleRating.RATING_TO_ARTICLE.eq(article))
                    .selectFirst(objectContext);

            return rating != null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
