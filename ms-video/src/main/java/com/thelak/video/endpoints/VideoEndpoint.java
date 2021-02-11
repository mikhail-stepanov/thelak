package com.thelak.video.endpoints;

import com.thelak.core.endpoints.MicroserviceAdvice;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbArticleView;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.thelak.video.services.VideoHelper.buildVideoModel;
import static com.thelak.video.services.VideoHelper.countView;

@RestController
@Api(value = "Video API", produces = "application/json")
public class VideoEndpoint extends MicroserviceAdvice implements IVideoService {

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

    @Override
    @ApiOperation(value = "Get video by id")
    @RequestMapping(value = VIDEO_GET, method = {RequestMethod.GET})
    public VideoModel get(@RequestParam Long id) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            long userId;
            UserInfo userInfo = null;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
                userId = userInfo.getUserId();
            } catch (Exception e) {
                userId = -1;
            }

            DbVideo dbVideo = SelectById.query(DbVideo.class, id).selectFirst(objectContext);

            if (dbVideo.getDeletedDate() != null) return null;

            DbVideoViews dbVideoViews = objectContext.newObject(DbVideoViews.class);
            dbVideoViews.setCreatedDate(LocalDateTime.now());
            dbVideoViews.setIdUser(userId);
            dbVideoViews.setViewToVideo(dbVideo);

            objectContext.commitChanges();

            dbVideo.setView(countView(dbVideo));

            objectContext.commitChanges();

            List<CategoryModel> categoryModel = categoryService.getByVideo(id);

            List<SpeakerModel> speakerModel = speakerService.getByVideo(id);

            return buildVideoModel(dbVideo, categoryModel, speakerModel, userInfo, true);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of video by ids")
    @RequestMapping(value = VIDEO_GET_IDS, method = {RequestMethod.GET})
    public List<VideoModel> getByIds(@RequestParam List<Long> ids) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            UserInfo userInfo = null;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception ignored) {
            }

            List<DbVideo> dbVideos;
            dbVideos = ObjectSelect.query(DbVideo.class).
                    where(ExpressionFactory.inDbExp(DbVideo.ID_PK_COLUMN, ids))
                    .and(DbVideo.DELETED_DATE.isNull())
                    .select(objectContext);

            List<VideoModel> videos = new ArrayList<>();

            UserInfo finalUserInfo = userInfo;
            dbVideos.forEach(dbVideo -> {
                List<CategoryModel> categoryModel = null;
                try {
                    categoryModel = categoryService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                List<SpeakerModel> speakerModel = null;
                try {
                    speakerModel = speakerService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                videos.add(buildVideoModel(dbVideo, categoryModel, speakerModel, finalUserInfo, false));
            });

            return videos;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of video by ids")
    @RequestMapping(value = VIDEO_SLIDER, method = {RequestMethod.GET})
    public List<VideoModel> getSliderVideos() throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            UserInfo userInfo = null;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception ignored) {
            }

            List<DbVideo> dbVideos;
            dbVideos = ObjectSelect.query(DbVideo.class).
                    where(DbVideo.POSTER_URL.isNotNull())
                    .and(DbVideo.DELETED_DATE.isNull())
                    .select(objectContext);

            List<VideoModel> videos = new ArrayList<>();

            UserInfo finalUserInfo = userInfo;
            dbVideos.forEach(dbVideo -> {
                List<CategoryModel> categoryModel = null;
                try {
                    categoryModel = categoryService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                List<SpeakerModel> speakerModel = null;
                try {
                    speakerModel = speakerService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                videos.add(buildVideoModel(dbVideo, categoryModel, speakerModel, finalUserInfo, false));
            });

            return videos;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
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
            ObjectContext objectContext = databaseService.getContext();

            UserInfo userInfo = null;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception ignored) {
            }

            final Expression categoryExpression;
            if (categoryFilter != null && categoryFilter.get(0) != 0) {
                List<Long> videoIdsByCategory = new ArrayList<>(categoryContentService.videoIds(categoryFilter));
                categoryExpression = ExpressionFactory.inDbExp(DbVideo.ID_PK_COLUMN, videoIdsByCategory);
            } else categoryExpression = DbVideo.TITLE.isNotNull();

            final Expression speakerExpression;
            if (speakerFilter != null && speakerFilter.get(0) != 0) {
                List<Long> videoIdsBySpeaker = new ArrayList<>(speakerContentService.videoIds(speakerFilter));
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
            if (languageFilter != null)
                languageFilterExpression = DbVideo.LANGUAGE.in(languageFilter);
            else languageFilterExpression = DbVideo.TITLE.isNotNull();


            List<DbVideo> dbVideos = null;
            if (sort != null) {
                if (sort == VideoSortEnum.NEW && sortType == VideoSortTypeEnum.ASC)
                    dbVideos = ObjectSelect.query(DbVideo.class)
                            .where(DbVideo.DELETED_DATE.isNull())
                            .and(speakerExpression)
                            .and(categoryExpression)
                            .and(countryFilterExpression)
                            .and(yearFilterExpression)
                            .and(playgroundFilterExpression)
                            .and(languageFilterExpression)
                            .orderBy(DbVideo.CREATED_DATE.asc())
                            .select(objectContext);
                if (sort == VideoSortEnum.NEW && sortType == VideoSortTypeEnum.DESC)
                    dbVideos = ObjectSelect.query(DbVideo.class)
                            .where(DbVideo.DELETED_DATE.isNull())
                            .and(speakerExpression)
                            .and(categoryExpression)
                            .and(countryFilterExpression)
                            .and(yearFilterExpression)
                            .and(playgroundFilterExpression)
                            .and(languageFilterExpression)
                            .orderBy(DbVideo.CREATED_DATE.desc())
                            .select(objectContext);
                if (sort == VideoSortEnum.DURATION && sortType == VideoSortTypeEnum.ASC)
                    dbVideos = ObjectSelect.query(DbVideo.class)
                            .where(DbVideo.DELETED_DATE.isNull())
                            .and(speakerExpression)
                            .and(categoryExpression)
                            .and(countryFilterExpression)
                            .and(yearFilterExpression)
                            .and(playgroundFilterExpression)
                            .and(languageFilterExpression)
                            .orderBy(DbVideo.DURATION.asc())
                            .select(objectContext);
                if (sort == VideoSortEnum.DURATION && sortType == VideoSortTypeEnum.DESC)
                    dbVideos = ObjectSelect.query(DbVideo.class)
                            .where(DbVideo.DELETED_DATE.isNull())
                            .and(speakerExpression)
                            .and(categoryExpression)
                            .and(countryFilterExpression)
                            .and(yearFilterExpression)
                            .and(playgroundFilterExpression)
                            .and(languageFilterExpression)
                            .orderBy(DbVideo.DURATION.desc())
                            .select(objectContext);
                if (sort == VideoSortEnum.POPULAR && sortType == VideoSortTypeEnum.ASC)
                    dbVideos = ObjectSelect.query(DbVideo.class)
                            .where(DbVideo.DELETED_DATE.isNull())
                            .and(speakerExpression)
                            .and(categoryExpression)
                            .and(countryFilterExpression)
                            .and(yearFilterExpression)
                            .and(playgroundFilterExpression)
                            .and(languageFilterExpression)
                            .orderBy(DbVideo.VIEW.asc())
                            .select(objectContext);
                if (sort == VideoSortEnum.POPULAR && sortType == VideoSortTypeEnum.DESC)
                    dbVideos = ObjectSelect.query(DbVideo.class)
                            .where(DbVideo.DELETED_DATE.isNull())
                            .and(speakerExpression)
                            .and(categoryExpression)
                            .and(countryFilterExpression)
                            .and(yearFilterExpression)
                            .and(playgroundFilterExpression)
                            .and(languageFilterExpression)
                            .orderBy(DbVideo.VIEW.desc())
                            .select(objectContext);
                if (sort == VideoSortEnum.RATING && sortType == VideoSortTypeEnum.ASC)
                    dbVideos = ObjectSelect.query(DbVideo.class)
                            .where(DbVideo.DELETED_DATE.isNull())
                            .and(speakerExpression)
                            .and(categoryExpression)
                            .and(countryFilterExpression)
                            .and(yearFilterExpression)
                            .and(playgroundFilterExpression)
                            .and(languageFilterExpression)
                            .orderBy(DbVideo.RATING.asc())
                            .select(objectContext);
                if (sort == VideoSortEnum.RATING && sortType == VideoSortTypeEnum.DESC)
                    dbVideos = ObjectSelect.query(DbVideo.class)
                            .where(DbVideo.DELETED_DATE.isNull())
                            .and(speakerExpression)
                            .and(categoryExpression)
                            .and(countryFilterExpression)
                            .and(yearFilterExpression)
                            .and(playgroundFilterExpression)
                            .and(languageFilterExpression)
                            .orderBy(DbVideo.RATING.desc())
                            .select(objectContext);
            } else
                dbVideos = ObjectSelect.query(DbVideo.class)
                        .where(DbVideo.DELETED_DATE.isNull())
                        .and(speakerExpression)
                        .and(categoryExpression)
                        .and(countryFilterExpression)
                        .and(yearFilterExpression)
                        .and(playgroundFilterExpression)
                        .and(languageFilterExpression)
                        .select(objectContext);

            if (page != null && size != null) {
                if (dbVideos.size() >= size * page)
                    dbVideos = dbVideos.subList(page * size - size, page * size);
                else if (dbVideos.size() >= size * (page - 1))
                    dbVideos = dbVideos.subList(page * size - size, dbVideos.size() - 1);
                else
                    dbVideos = new ArrayList<>();
            }

            List<VideoModel> videos = new ArrayList<>();

            UserInfo finalUserInfo = userInfo;
            if (dbVideos != null) {
                dbVideos.forEach(dbVideo -> {
                    List<CategoryModel> categoryModel = null;
                    try {
                        categoryModel = categoryService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                    } catch (MicroServiceException e) {
                        log.error(e.staticMessage());
                    }
                    List<SpeakerModel> speakerModel = null;
                    try {
                        speakerModel = speakerService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                    } catch (MicroServiceException e) {
                        log.error(e.staticMessage());
                    }
                    videos.add(buildVideoModel(dbVideo, categoryModel, speakerModel, finalUserInfo, false));
                });
            }

            return videos;

        } catch (Exception e) {
            e.printStackTrace();
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
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
            ObjectContext objectContext = databaseService.getContext();

            UserInfo userInfo = null;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception ignored) {
            }

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
                if (dbVideos.size() >= size * page)
                    dbVideos = dbVideos.subList(page * size - size, page * size);
                else if (dbVideos.size() >= size * (page - 1))
                    dbVideos = dbVideos.subList(page * size - size, dbVideos.size());
                else
                    dbVideos = new ArrayList<>();
            }

            List<VideoModel> videos = new ArrayList<>();

            UserInfo finalUserInfo = userInfo;
            dbVideos.forEach(dbVideo -> {
                List<CategoryModel> categoryModel = null;
                try {
                    categoryModel = categoryService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                List<SpeakerModel> speakerModel = null;
                try {
                    speakerModel = speakerService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                } catch (MicroServiceException e) {
                    log.error(e.staticMessage());
                }
                videos.add(buildVideoModel(dbVideo, categoryModel, speakerModel, finalUserInfo, false));
            });

            return videos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Create video")
    @RequestMapping(value = VIDEO_CREATE, method = {RequestMethod.POST})
    public VideoModel create(@RequestBody VideoCreateRequest request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            UserInfo userInfo = null;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception ignored) {
            }

            DbVideo dbVideo = objectContext.newObject(DbVideo.class);
            dbVideo.setTitle(request.getTitle());
            dbVideo.setDescription(request.getDescription());
            dbVideo.setYear(request.getYear());
            dbVideo.setLanguage(request.getLanguage());
            dbVideo.setCountry(request.getCountry());
            dbVideo.setDuration(request.getDuration());
            dbVideo.setPlayground(request.getPlayground());
            dbVideo.setPartnerLogoUrl(request.getPartnerLogoUrl());
            dbVideo.setCoverUrl(request.getCoverUrl());
            dbVideo.setPosterUrl(request.getPosterUrl());
            dbVideo.setIsSubscription(request.getSubscription());
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

            if (request.getCategory() != null)
                request.getCategory().forEach(categoryModel -> {
                    try {
                        categoryContentService.videoToCategoryAdd((Long) dbVideo.getObjectId().getIdSnapshot().get("id"),
                                categoryModel.getId());
                    } catch (MicroServiceException e) {
                        e.printStackTrace();
                    }
                });

            List<CategoryModel> categoryModel = categoryService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));

            List<SpeakerModel> speakerModel = speakerService.getByVideo((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));

            return buildVideoModel(dbVideo, categoryModel, speakerModel, userInfo, true);
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Update video by id")
    @RequestMapping(value = VIDEO_UPDATE, method = {RequestMethod.PUT})
    public VideoModel update(@RequestBody VideoModel request) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            UserInfo userInfo = null;
            try {
                userInfo = (UserInfo) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
            } catch (Exception ignored) {
            }

            DbVideo dbVideo = SelectById.query(DbVideo.class, request.getId()).selectFirst(objectContext);

            if (request.getSources() != null) {
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
            }

            dbVideo.setTitle(Optional.ofNullable(request.getTitle()).orElse(dbVideo.getTitle()));
            dbVideo.setDescription(Optional.ofNullable(request.getDescription()).orElse(dbVideo.getDescription()));
            dbVideo.setYear(Optional.ofNullable(request.getYear()).orElse(dbVideo.getYear()));
            dbVideo.setLanguage(Optional.ofNullable(request.getLanguage()).orElse(dbVideo.getLanguage()));
            dbVideo.setCountry(Optional.ofNullable(request.getCountry()).orElse(dbVideo.getCountry()));
            dbVideo.setDuration(Optional.ofNullable(request.getDuration()).orElse(dbVideo.getDuration()));
            dbVideo.setPlayground(Optional.ofNullable(request.getPlayground()).orElse(dbVideo.getPlayground()));
            dbVideo.setPartnerLogoUrl(Optional.ofNullable(request.getPartnerLogoUrl()).orElse(dbVideo.getPartnerLogoUrl()));
            dbVideo.setCoverUrl(Optional.ofNullable(request.getCoverUrl()).orElse(dbVideo.getCoverUrl()));
            dbVideo.setPosterUrl(Optional.ofNullable(request.getPosterUrl()).orElse(dbVideo.getPosterUrl()));
            dbVideo.setIsSubscription(Optional.ofNullable(request.getSubscription()).orElse(dbVideo.isIsSubscription()));
            dbVideo.setModifiedDate(LocalDateTime.now());

            objectContext.commitChanges();
            if (request.getCategory() != null) {
                categoryContentService.videoToCategoryDelete((Long) dbVideo.getObjectId().getIdSnapshot().get("id"));
                request.getCategory().forEach(categoryModel -> {
                    try {
                        categoryContentService.videoToCategoryAdd((Long) dbVideo.getObjectId().getIdSnapshot().get("id"),
                                categoryModel.getId());
                    } catch (MicroServiceException e) {
                        e.printStackTrace();
                    }
                });
            }
            List<CategoryModel> categoryModels = categoryService.getByVideo(request.getId());

            List<SpeakerModel> speakerModel = speakerService.getByVideo(request.getId());

            return buildVideoModel(dbVideo, categoryModels, speakerModel, userInfo, true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Delete video by id")
    @RequestMapping(value = VIDEO_DELETE, method = {RequestMethod.DELETE})
    public Boolean delete(@RequestParam Long id) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            DbVideo dbVideo = SelectById.query(DbVideo.class, id).selectFirst(objectContext);

            dbVideo.setDeletedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get video filters")
    @RequestMapping(value = VIDEO_FILTER_GET, method = {RequestMethod.GET})
    public VideoFilterModel getFilters() throws MicroServiceException {
        ObjectContext objectContext = databaseService.getContext();

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

    @ApiOperation(value = "Get videos view count by userId")
    @RequestMapping(value = VIDEO_STAT_VIEWS, method = {RequestMethod.GET})
    public HashMap<String, Integer> getViewCount(@RequestParam List<Long> ids) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            HashMap<String, Integer> result = new HashMap<>();
            ids.forEach(id -> {
                List<DbVideoViews> dbVideoViews = ObjectSelect.query(DbVideoViews.class)
                        .where(DbVideoViews.ID_USER.eq(id))
                        .select(objectContext);
                result.put(id.toString(), dbVideoViews.size());
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @ApiOperation(value = "Get videos last view by userId")
    @RequestMapping(value = VIDEO_STAT_LAST, method = {RequestMethod.GET})
    public HashMap<String, String> getLastView(@RequestParam List<Long> ids) throws MicroServiceException {
        try {
            ObjectContext objectContext = databaseService.getContext();

            HashMap<String, String> result = new HashMap<>();
            ids.forEach(id -> {
                DbVideoViews dbVideoViews = null;
                try {
                    dbVideoViews = ObjectSelect.query(DbVideoViews.class)
                            .where(DbVideoViews.ID_USER.eq(id))
                            .orderBy(DbVideoViews.CREATED_DATE.desc())
                            .selectFirst(objectContext);
                }catch (Exception ingored){}
                result.put(id.toString(), dbVideoViews != null ? dbVideoViews.getCreatedDate().toString() : null);
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsInternalErrorException(e.getMessage());
        }
    }
}
