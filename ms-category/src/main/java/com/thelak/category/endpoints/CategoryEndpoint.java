package com.thelak.category.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbCategory;
import com.thelak.database.entity.DbCategoryArticles;
import com.thelak.database.entity.DbCategoryEvents;
import com.thelak.database.entity.DbCategoryVideos;
import com.thelak.route.category.interfaces.ICategoryService;
import com.thelak.route.category.models.CategoryCreateModel;
import com.thelak.route.category.models.CategoryModel;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
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

import static com.thelak.category.services.CategoryHelper.buildCategoryModel;

@RestController
@Api(value = "Category API", produces = "application/json")
public class CategoryEndpoint extends AbstractMicroservice implements ICategoryService {

    @Autowired
    private DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(CategoryEndpoint.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get category by id")
    @RequestMapping(value = CATEGORY_GET, method = {RequestMethod.GET})
    public CategoryModel get(@RequestParam Long id) throws MicroServiceException {
        try {
            DbCategory dbCategory = SelectById.query(DbCategory.class, id).selectFirst(objectContext);

            return buildCategoryModel(dbCategory);

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while get category");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get category by videoId")
    @RequestMapping(value = CATEGORY_GET, method = {RequestMethod.GET})
    public CategoryModel getByVideo(@RequestParam Long videoId) throws MicroServiceException {
        try {
            DbCategoryVideos dbCategoryVideos = ObjectSelect.query(DbCategoryVideos.class)
                    .where(DbCategoryVideos.ID_VIDEO.eq(videoId)).selectFirst(objectContext);

            return buildCategoryModel(dbCategoryVideos.getVideoToCategory());

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while get category");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get category by articleId")
    @RequestMapping(value = CATEGORY_GET, method = {RequestMethod.GET})
    public CategoryModel getByArticle(@RequestParam Long articleId) throws MicroServiceException {
        try {
            DbCategoryArticles categoryArticles = ObjectSelect.query(DbCategoryArticles.class)
                    .where(DbCategoryArticles.ID_ARTICLE.eq(articleId)).selectFirst(objectContext);

            return buildCategoryModel(categoryArticles.getArticleToCategory());

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while get category");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get category by eventId")
    @RequestMapping(value = CATEGORY_GET, method = {RequestMethod.GET})
    public CategoryModel getByEvent(@RequestParam Long eventId) throws MicroServiceException {
        try {
            DbCategoryEvents dbCategoryEvents = ObjectSelect.query(DbCategoryEvents.class)
                    .where(DbCategoryEvents.ID_EVENT.eq(eventId)).selectFirst(objectContext);

            return buildCategoryModel(dbCategoryEvents.getEventToCategory());

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while get category");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get list of categories")
    @RequestMapping(value = CATEGORY_LIST, method = {RequestMethod.GET})
    public List<CategoryModel> list() throws MicroServiceException {
        try {
            List<DbCategory> dbCategories;
            dbCategories = ObjectSelect.query(DbCategory.class).
                    where(DbCategory.DELETED_DATE.isNull())
                    .select(objectContext);

            List<CategoryModel> categoryModels = new ArrayList<>();

            dbCategories.forEach(dbCategory -> {
                categoryModels.add(buildCategoryModel(dbCategory));
            });

            return categoryModels;
        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while getting list of categories");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Find category by title")
    @RequestMapping(value = CATEGORY_SEARCH, method = {RequestMethod.GET})
    public List<CategoryModel> search(@RequestParam String search) throws MicroServiceException {
        try {

            List<DbCategory> dbCategories;
            dbCategories = ObjectSelect.query(DbCategory.class).
                    where(DbCategory.DELETED_DATE.isNull())
                    .or(DbCategory.TITLE.containsIgnoreCase(search.toLowerCase()))
                    .select(objectContext);

            List<CategoryModel> categoryModels = new ArrayList<>();

            dbCategories.forEach(dbCategory -> {
                categoryModels.add(buildCategoryModel(dbCategory));
            });

            return categoryModels;
        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while searching categories");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Create category")
    @RequestMapping(value = CATEGORY_CREATE, method = {RequestMethod.POST})
    public CategoryModel create(@RequestBody CategoryCreateModel request) throws MicroServiceException {
        try {
            DbCategory dbCategory = objectContext.newObject(DbCategory.class);
            dbCategory.setTitle(request.getTitle());
            dbCategory.setCoverUrl(request.getImageUrl());
            dbCategory.setCreatedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return buildCategoryModel(dbCategory);

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while create category: " + e.getLocalizedMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Update category by id")
    @RequestMapping(value = CATEGORY_UPDATE, method = {RequestMethod.PUT})
    public CategoryModel update(@RequestBody CategoryModel request) throws MicroServiceException {
        try {

            DbCategory dbCategory = SelectById.query(DbCategory.class, request.getId()).selectFirst(objectContext);

            dbCategory.setTitle(request.getTitle());
            dbCategory.setCoverUrl(request.getImageUrl());
            dbCategory.setModifiedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return buildCategoryModel(dbCategory);

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while updating category");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Delete category by id")
    @RequestMapping(value = CATEGORY_DELETE, method = {RequestMethod.DELETE})
    public Boolean delete(@RequestParam Long id) throws MicroServiceException {
        try {
            DbCategory dbCategory = SelectById.query(DbCategory.class, id).selectFirst(objectContext);

            dbCategory.setDeletedDate(LocalDateTime.now());
            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while deleting category");
        }
    }
}
