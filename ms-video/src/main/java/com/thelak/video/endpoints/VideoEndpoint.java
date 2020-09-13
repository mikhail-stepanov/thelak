package com.thelak.video.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbVideo;
import com.thelak.database.entity.DbVideoViews;
import com.thelak.route.category.interfaces.ICategoryContentService;
import com.thelak.route.category.interfaces.ICategoryService;
import com.thelak.route.category.models.CategoryModel;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.speaker.interfaces.ISpeakerContentService;
import com.thelak.route.speaker.interfaces.ISpeakerService;
import com.thelak.route.speaker.models.SpeakerModel;
import com.thelak.route.video.enums.VideoSortEnum;
import com.thelak.route.video.enums.VideoSortTypeEnum;
import com.thelak.route.video.interfaces.IVideoService;
import com.thelak.route.video.models.VideoCreateRequest;
import com.thelak.route.video.models.VideoFilterModel;
import com.thelak.route.video.models.VideoModel;
import com.thelak.route.video.models.VideoSourceModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.thelak.video.services.VideoHelper.buildVideoModel;

@RestController
@Api(value = "Video API", produces = "application/json")
public class VideoEndpoint extends AbstractMicroservice implements IVideoService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private ICategoryContentService categoryContentService;

    @Autowired
    private ISpeakerContentService speakerContentService;

    @Autowired
    private ISpeakerService speakerService;

    @Autowired
    private ICategoryService categoryService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(VideoEndpoint.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get video by id")
    @RequestMapping(value = VIDEO_GET, method = {RequestMethod.GET})
    public VideoModel get(@RequestParam Long id) throws MicroServiceException {
        try {
            long userId;
            try {
                UserInfo userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
                userId = userInfo.getUserId();
            } catch (Exception e) {
                userId = -1;
            }

            DbVideo dbVideo = SelectById.query(DbVideo.class, id).selectFirst(objectContext);

            DbVideoViews dbVideoViews = objectContext.newObject(DbVideoViews.class);
            dbVideoViews.setCreatedDate(LocalDateTime.now());
            dbVideoViews.setIdUser(userId);
            dbVideoViews.setViewToVideo(dbVideo);

            objectContext.commitChanges();

            CategoryModel categoryModel = categoryService.getByVideo(id);

            SpeakerModel speakerModel = speakerService.getByVideo(id);

            return buildVideoModel(dbVideo, categoryModel, speakerModel);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get list of video by ids")
    @RequestMapping(value = VIDEO_GET_IDS, method = {RequestMethod.GET})
    public List<VideoModel> getByIds(@RequestParam List<Long> ids) throws MicroServiceException {
        try {
            List<DbVideo> dbVideos;
            dbVideos = ObjectSelect.query(DbVideo.class).
                    where(ExpressionFactory.inDbExp(DbVideo.ID_PK_COLUMN, ids))
                    .select(objectContext);

            List<VideoModel> videos = new ArrayList<>();

            dbVideos.forEach(dbVideo -> {
                CategoryModel categoryModel = null;
                try {
                    categoryModel = categoryService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                SpeakerModel speakerModel = null;
                try {
                    speakerModel = speakerService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                videos.add(buildVideoModel(dbVideo, categoryModel, speakerModel));
            });

            return videos;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get list of videos")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "page",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "size",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "sort",
                    dataType = "com.thelak.route.video.enums.VideoSortEnum",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "sortType",
                    dataType = "com.thelak.route.video.enums.VideoSortTypeEnum",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "countryFilter",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "yearFilter",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "playgroundFilter",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "languageFilter",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "categoryFilter",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "speakerFilter",
                    paramType = "query")})
    @RequestMapping(value = VIDEO_LIST, method = {RequestMethod.GET})
    public List<VideoModel> list(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size,
                                 @RequestParam(required = false) VideoSortEnum sort, @RequestParam(required = false) VideoSortTypeEnum sortType,
                                 @RequestParam(required = false) List<String> countryFilter, @RequestParam(required = false) List<Integer> yearFilter,
                                 @RequestParam(required = false) List<String> playgroundFilter, @RequestParam(required = false) List<String> languageFilter,
                                 @RequestParam(required = false) List<Long> categoryFilter, @RequestParam(required = false) List<Long> speakerFilter) throws MicroServiceException {
        try {

            List<Long> videoIdsByCategory = new ArrayList<>();
            final Expression categoryExpression;
            if (categoryFilter != null && categoryFilter.get(0) != 0) {
                videoIdsByCategory.addAll(categoryContentService.videoIds(categoryFilter));
                categoryExpression = ExpressionFactory.inDbExp(DbVideo.ID_PK_COLUMN, videoIdsByCategory);
            } else categoryExpression = DbVideo.TITLE.isNotNull();

            List<Long> videoIdsBySpeaker = new ArrayList<>();
            final Expression speakerExpression;
            if (speakerFilter != null && speakerFilter.get(0) != 0) {
                videoIdsBySpeaker.addAll(speakerContentService.videoIds(categoryFilter));
                speakerExpression = ExpressionFactory.inDbExp(DbVideo.ID_PK_COLUMN, videoIdsBySpeaker);
            } else speakerExpression = DbVideo.TITLE.isNotNull();

            final Expression countryFilterExpression;
            if (countryFilter != null)
                countryFilterExpression = DbVideo.COUNTRY.in(countryFilter);
            else countryFilterExpression = DbVideo.TITLE.isNotNull();

            final Expression yearFilterExpression;
            if (yearFilter != null)
                yearFilterExpression = DbVideo.YEAR.in(yearFilter);
            else yearFilterExpression = DbVideo.TITLE.isNotNull();

            final Expression playgroundFilterExpression;
            if (playgroundFilter != null)
                playgroundFilterExpression = DbVideo.PLAYGROUND.in(playgroundFilter);
            else playgroundFilterExpression = DbVideo.TITLE.isNotNull();

            final Expression languageFilterExpression;
            if (playgroundFilter != null)
                languageFilterExpression = DbVideo.PLAYGROUND.in(playgroundFilter);
            else languageFilterExpression = DbVideo.TITLE.isNotNull();


            List<DbVideo> dbVideos;
            if (page == null || size == null)
                dbVideos = ObjectSelect.query(DbVideo.class)
                        .where(countryFilterExpression)
                        .and(yearFilterExpression)
                        .and(playgroundFilterExpression)
                        .and(playgroundFilterExpression)
                        .and(speakerExpression)
                        .and(categoryExpression)
                        .pageSize(30)
                        .select(objectContext);
            else {
                dbVideos = ObjectSelect.query(DbVideo.class)
                        .where(countryFilterExpression)
                        .and(yearFilterExpression)
                        .and(playgroundFilterExpression)
                        .and(languageFilterExpression)
                        .and(speakerExpression)
                        .and(categoryExpression)
                        .pageSize(size)
                        .select(objectContext);
                dbVideos = dbVideos.subList(page * size - size, page * size);
            }

            List<VideoModel> videos = new ArrayList<>();

            dbVideos.forEach(dbVideo -> {
                CategoryModel categoryModel = null;
                try {
                    categoryModel = categoryService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                SpeakerModel speakerModel = null;
                try {
                    speakerModel = speakerService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                videos.add(buildVideoModel(dbVideo, categoryModel, speakerModel));
            });

            if (sort != null) {
                if (sort == VideoSortEnum.NEW) {
                    videos.sort(Comparators.NEW);
                    if (sortType == VideoSortTypeEnum.DESC)
                        Collections.reverse(videos);
                }
                if (sort == VideoSortEnum.POPULAR) {
                    videos.sort(Comparators.POPULAR);
                    if (sortType == VideoSortTypeEnum.DESC)
                        Collections.reverse(videos);
                }
                if (sort == VideoSortEnum.RATING) {
                    videos.sort(Comparators.RATING);
                    if (sortType == VideoSortTypeEnum.DESC)
                        Collections.reverse(videos);
                }
                if (sort == VideoSortEnum.DURATION) {
                    videos.sort(Comparators.DURATION);
                    if (sortType == VideoSortTypeEnum.DESC)
                        Collections.reverse(videos);
                }
            }

            return videos;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Find videos by title/description/speaker")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "page",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "size",
                    paramType = "query")})
    @RequestMapping(value = VIDEO_SEARCH, method = {RequestMethod.GET})
    public List<VideoModel> search(@RequestParam String search, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) throws MicroServiceException {
        try {

            List<DbVideo> dbVideos;
            if (page == null || size == null)
                dbVideos = ObjectSelect.query(DbVideo.class).
                        where(DbVideo.DELETED_DATE.isNull())
                        .and(DbVideo.DESCRIPTION.containsIgnoreCase(search.toLowerCase()))
                        .or(DbVideo.TITLE.containsIgnoreCase(search.toLowerCase()))
                        .or(DbVideo.SPEAKER.containsIgnoreCase(search.toLowerCase()))
                        .pageSize(30)
                        .select(objectContext);
            else {
                dbVideos = ObjectSelect.query(DbVideo.class).
                        where(DbVideo.DELETED_DATE.isNull())
                        .and(DbVideo.DESCRIPTION.containsIgnoreCase(search.toLowerCase()))
                        .or(DbVideo.TITLE.containsIgnoreCase(search.toLowerCase()))
                        .or(DbVideo.SPEAKER.containsIgnoreCase(search.toLowerCase()))
                        .pageSize(size)
                        .select(objectContext);
                dbVideos = dbVideos.subList(page * size - size, page * size);
            }

            List<VideoModel> videos = new ArrayList<>();

            dbVideos.forEach(dbVideo -> {
                CategoryModel categoryModel = null;
                try {
                    categoryModel = categoryService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                SpeakerModel speakerModel = null;
                try {
                    speakerModel = speakerService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                videos.add(buildVideoModel(dbVideo, categoryModel, speakerModel));
            });

            return videos;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Create video")
    @RequestMapping(value = VIDEO_CREATE, method = {RequestMethod.POST})
    public VideoModel create(@RequestBody VideoCreateRequest request) throws MicroServiceException {
        try {

            DbVideo dbVideo = objectContext.newObject(DbVideo.class);
            dbVideo.setTitle(request.getTitle());
            dbVideo.setDescription(request.getDescription());
            dbVideo.setYear(request.getYear());
            dbVideo.setCountry(request.getCountry());
            dbVideo.setDuration(request.getDuration());
            dbVideo.setCategory(request.getCategory());
            dbVideo.setSpeaker(request.getSpeaker());
            dbVideo.setSpeakerInformation(request.getSpeakerInformation());
            dbVideo.setPartnerLogoUrl(request.getPartnerLogoUrl());
            dbVideo.setCoverUrl(request.getCoverUrl());
            dbVideo.setCreatedDate(LocalDateTime.now());

            List<VideoSourceModel> sources = request.getSources();
            sources.forEach(source -> {
                if (source.getRes() == 360)
                    dbVideo.setContentUrl360(source.getSrc());
                if (source.getRes() == 480)
                    dbVideo.setContentUrl480(source.getSrc());
                if (source.getRes() == 720)
                    dbVideo.setContentUrl720(source.getSrc());
                if (source.getRes() == 1080)
                    dbVideo.setContentUrl1080(source.getSrc());
            });

            objectContext.commitChanges();

            CategoryModel categoryModel = categoryService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));

            SpeakerModel speakerModel = speakerService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));

            return buildVideoModel(dbVideo, categoryModel, speakerModel);
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Update video by id")
    @RequestMapping(value = VIDEO_UPDATE, method = {RequestMethod.PUT})
    public VideoModel update(@RequestBody VideoModel request) throws MicroServiceException {
        try {

            DbVideo dbVideo = SelectById.query(DbVideo.class, request.getId()).selectFirst(objectContext);

            List<VideoSourceModel> sources = request.getSources();
            sources.forEach(source -> {
                if (source.getRes() == 360)
                    dbVideo.setContentUrl360(source.getSrc());
                if (source.getRes() == 480)
                    dbVideo.setContentUrl480(source.getSrc());
                if (source.getRes() == 720)
                    dbVideo.setContentUrl720(source.getSrc());
                if (source.getRes() == 1080)
                    dbVideo.setContentUrl1080(source.getSrc());
            });

            dbVideo.setTitle(request.getTitle());
            dbVideo.setDescription(request.getDescription());
            dbVideo.setYear(request.getYear());
            dbVideo.setCountry(request.getCountry());
            dbVideo.setDuration(request.getDuration());
            dbVideo.setPartnerLogoUrl(request.getPartnerLogoUrl());
            dbVideo.setCoverUrl(request.getCoverUrl());
            dbVideo.setCreatedDate(LocalDateTime.now());

            objectContext.commitChanges();

            CategoryModel categoryModel = categoryService.getByVideo(request.getId());

            SpeakerModel speakerModel = speakerService.getByVideo(request.getId());

            return buildVideoModel(dbVideo, categoryModel, speakerModel);
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Delete video by id")
    @RequestMapping(value = VIDEO_DELETE, method = {RequestMethod.DELETE})
    public Boolean delete(@RequestParam Long id) throws MicroServiceException {
        try {

            DbVideo dbVideo = SelectById.query(DbVideo.class, id).selectFirst(objectContext);

            dbVideo.setDeletedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get video filters")
    @RequestMapping(value = VIDEO_FILTER_GET, method = {RequestMethod.GET})
    public VideoFilterModel getFilters() throws MicroServiceException {

        List<String> countries = ObjectSelect.columnQuery(DbVideo.class, DbVideo.COUNTRY).distinct().select(objectContext);
        List<String> playgrounds = ObjectSelect.columnQuery(DbVideo.class, DbVideo.PLAYGROUND).distinct().select(objectContext);
        List<String> languages = ObjectSelect.columnQuery(DbVideo.class, DbVideo.LANGUAGE).distinct().select(objectContext);
        List<Integer> years = ObjectSelect.columnQuery(DbVideo.class, DbVideo.YEAR).distinct().select(objectContext);

        return VideoFilterModel.builder()
                .countries(countries)
                .playgrounds(playgrounds)
                .languages(languages)
                .years(years)
                .build();
    }

    public static class Comparators {
        public static final Comparator<VideoModel> POPULAR = Comparator.comparing(VideoModel::getViewsCount);
        public static final Comparator<VideoModel> DURATION = Comparator.comparing(VideoModel::getDuration);
        public static final Comparator<VideoModel> RATING = Comparator.comparing(VideoModel::getRating);
        public static final Comparator<VideoModel> NEW = Comparator.comparing(VideoModel::getCreatedDate);

    }
}
