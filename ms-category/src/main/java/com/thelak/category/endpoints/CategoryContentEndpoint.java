package com.thelak.category.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@Api(value = "Category content API", produces = "application/json")
public class CategoryContentEndpoint extends AbstractMicroservice implements ICategoryContentService {

    @Autowired
    private DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(CategoryContentEndpoint.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    @ApiOperation(value = "Get video ids by category ids")
    @RequestMapping(value = CATEGORY_VIDEO, method = {RequestMethod.GET})
    public List<Long> videoIds(@RequestParam List<Long> categoryIds) throws MicroServiceException {
        try {
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
            List<DbCategory> dbCategories = ObjectSelect.query(DbCategory.class).
                    where(ExpressionFactory.inDbExp(DbCategory.ID_PK_COLUMN, categoryIds))
                    .select(objectContext);

            return ObjectSelect.columnQuery(DbCategoryEvents.class, DbCategoryEvents.ID_EVENT)
                    .where(DbCategoryEvents.EVENT_TO_CATEGORY.in(dbCategories)).select(objectContext);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }
}
