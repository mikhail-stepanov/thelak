package com.thelak.video.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.core.models.UserInfo;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.*;
import com.thelak.route.category.interfaces.ICategoryService;
import com.thelak.route.category.models.CategoryModel;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsBadRequestException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.speaker.interfaces.ISpeakerService;
import com.thelak.route.speaker.models.SpeakerModel;
import com.thelak.route.video.interfaces.IVideoFunctionsService;
import com.thelak.route.video.models.VideoModel;
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
import java.util.ArrayList;
import java.util.List;

import static com.thelak.video.services.VideoHelper.buildVideoModel;

@RestController
@Api(value = "Video functions API", produces = "application/json")
public class VideoFunctionsEndpoint extends AbstractMicroservice implements IVideoFunctionsService {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ISpeakerService speakerService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(VideoFunctionsEndpoint.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Add video to favorite")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = VIDEO_FAVORITES_ADD, method = {RequestMethod.POST})
    public Boolean addFavorites(@RequestParam Long videoId) throws MicroServiceException {
        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbVideo video = SelectById.query(DbVideo.class, videoId).selectFirst(objectContext);

        if (!checkInFavorites(video, userInfo.getUserId())) {

            DbVideoFavorites favorites = objectContext.newObject(DbVideoFavorites.class);

            favorites.setCreatedDate(LocalDateTime.now());
            favorites.setIdUser(userInfo.getUserId());
            favorites.setFavoriteToVideo(video);

            objectContext.commitChanges();

            return true;

        } else
            throw new MsBadRequestException("Always in favorites");
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get list of favorites videos")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = VIDEO_FAVORITES_LIST, method = {RequestMethod.GET})
    public List<VideoModel> listFavorites() throws MicroServiceException {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            List<DbVideoFavorites> dbVideoFavorites = ObjectSelect.query(DbVideoFavorites.class)
                    .where(DbVideoFavorites.ID_USER.eq(userInfo.getUserId())).select(objectContext);

            List<VideoModel> videos = new ArrayList<>();

            dbVideoFavorites.forEach(favorites -> {
                DbVideo dbVideo = favorites.getFavoriteToVideo();
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
    @ApiOperation(value = "Check video is favorite")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = VIDEO_FAVORITES_CHECK, method = {RequestMethod.GET})
    public Boolean checkFavorites(@RequestParam Long videoId) throws MicroServiceException {
        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbVideo video = SelectById.query(DbVideo.class, videoId).selectFirst(objectContext);

        return checkInFavorites(video, userInfo.getUserId());
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Delete video from favorite")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = VIDEO_FAVORITES_DELETE, method = {RequestMethod.DELETE})
    public Boolean deleteFavorites(@RequestParam Long videoId) throws MicroServiceException {

        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbVideo video = SelectById.query(DbVideo.class, videoId).selectFirst(objectContext);

        DbVideoFavorites favorites = ObjectSelect.query(DbVideoFavorites.class)
                .where(DbVideoFavorites.ID_USER.eq(userInfo.getUserId()))
                .and(DbVideoFavorites.FAVORITE_TO_VIDEO.eq(video))
                .selectFirst(objectContext);

        objectContext.deleteObject(favorites);

        objectContext.commitChanges();

        return true;
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Add video to view history")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = VIDEO_HISTORY_ADD, method = {RequestMethod.POST})
    public Boolean addHistory(@RequestParam Long videoId) throws MicroServiceException {

        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbVideo video = SelectById.query(DbVideo.class, videoId).selectFirst(objectContext);

        DbVideoHistory history = objectContext.newObject(DbVideoHistory.class);

        history.setCreatedDate(LocalDateTime.now());
        history.setIdUser(userInfo.getUserId());
        history.setHistoryToVideo(video);

        objectContext.commitChanges();

        return true;
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get list of viewed videos")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = VIDEO_HISTORY_LIST, method = {RequestMethod.GET})
    public List<VideoModel> listHistory() throws MicroServiceException {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            List<DbVideoHistory> dbVideoHistories = ObjectSelect.query(DbVideoHistory.class)
                    .where(DbVideoHistory.ID_USER.eq(userInfo.getUserId())).select(objectContext);

            List<VideoModel> videos = new ArrayList<>();

            dbVideoHistories.forEach(history -> {
                DbVideo dbVideo = history.getHistoryToVideo();
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
    @ApiOperation(value = "Add timecode of video view")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = VIDEO_TIMECODE_ADD, method = {RequestMethod.POST})
    public Boolean addTimeCode(@RequestParam Long videoId, @RequestParam String timecode) throws MicroServiceException {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            DbVideo video = SelectById.query(DbVideo.class, videoId).selectFirst(objectContext);

            try {
                DbVideoTimecode dbVideoTimecode = ObjectSelect.query(DbVideoTimecode.class)
                        .where(DbVideoTimecode.ID_USER.eq(userInfo.getUserId()))
                        .and(DbVideoTimecode.TIMECODE_TO_VIDEO.eq(video))
                        .selectFirst(objectContext);

                dbVideoTimecode.setTimecode(timecode);

                objectContext.commitChanges();

                return true;
            } catch (Exception e) {
                DbVideoTimecode dbVideoTimecode = objectContext.newObject(DbVideoTimecode.class);

                dbVideoTimecode.setCreatedDate(LocalDateTime.now());
                dbVideoTimecode.setIdUser(userInfo.getUserId());
                dbVideoTimecode.setTimecodeToVideo(video);
                dbVideoTimecode.setTimecode(timecode);

                objectContext.commitChanges();

                return true;
            }
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get timecode of video view")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = VIDEO_TIMECODE_GET, method = {RequestMethod.GET})
    public String getTimeCode(@RequestParam Long videoId) throws MicroServiceException {

        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbVideo video = SelectById.query(DbVideo.class, videoId).selectFirst(objectContext);

        DbVideoTimecode timecode = ObjectSelect.query(DbVideoTimecode.class)
                .where(DbVideoTimecode.ID_USER.eq(userInfo.getUserId()))
                .and(DbVideoTimecode.TIMECODE_TO_VIDEO.eq(video))
                .selectFirst(objectContext);

        return timecode.getTimecode();
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Rate Video")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = VIDEO_RATING_ADD, method = {RequestMethod.POST})
    public Boolean addRating(@RequestParam Long videoId, @RequestParam Integer score) throws MicroServiceException {
        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbVideo video = SelectById.query(DbVideo.class, videoId).selectFirst(objectContext);

        if (!checkIsRate(video, userInfo.getUserId())) {

            DbVideoRating rating = objectContext.newObject(DbVideoRating.class);

            rating.setCreatedDate(LocalDateTime.now());
            rating.setIdUser(userInfo.getUserId());
            rating.setScore(score);
            rating.setRatingToVideo(video);

            objectContext.commitChanges();

            return true;

        } else
            throw new MsBadRequestException("Always in favorites");
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Delete rating from video")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = VIDEO_RATING_DELETE, method = {RequestMethod.DELETE})
    public Boolean deleteRating(@RequestParam Long videoId) throws MicroServiceException {
        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbVideo video = SelectById.query(DbVideo.class, videoId).selectFirst(objectContext);

        DbVideoRating rating = ObjectSelect.query(DbVideoRating.class)
                .where(DbVideoRating.ID_USER.eq(userInfo.getUserId()))
                .and(DbVideoRating.RATING_TO_VIDEO.eq(video))
                .selectFirst(objectContext);

        objectContext.deleteObject(rating);

        objectContext.commitChanges();

        return true;
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Check video is favorite")
    @ApiImplicitParams(
            {@ApiImplicitParam(required = true,
                    defaultValue = "Bearer ",
                    name = "Authorization",
                    paramType = "header")}
    )
    @RequestMapping(value = VIDEO_RATING_CHECK, method = {RequestMethod.GET})
    public Boolean checkRating(@RequestParam Long videoId) throws MicroServiceException {
        UserInfo userInfo = (UserInfo) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        DbVideo video = SelectById.query(DbVideo.class, videoId).selectFirst(objectContext);

        return checkIsRate(video, userInfo.getUserId());
    }

    private boolean checkInFavorites(DbVideo video, Long userId) {
        try {
            DbVideoFavorites favorites = ObjectSelect.query(DbVideoFavorites.class)
                    .where(DbVideoFavorites.ID_USER.eq(userId))
                    .and(DbVideoFavorites.FAVORITE_TO_VIDEO.eq(video))
                    .selectFirst(objectContext);

            return favorites != null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private boolean checkIsRate(DbVideo video, Long userId) {
        try {
            DbVideoRating rating = ObjectSelect.query(DbVideoRating.class)
                    .where(DbVideoRating.ID_USER.eq(userId))
                    .and(DbVideoRating.RATING_TO_VIDEO.eq(video))
                    .selectFirst(objectContext);

            return rating != null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private boolean checkIsTimecode(DbVideo video, Long userId) {
        try {
            DbVideoTimecode timecode = ObjectSelect.query(DbVideoTimecode.class)
                    .where(DbVideoTimecode.ID_USER.eq(userId))
                    .and(DbVideoTimecode.TIMECODE_TO_VIDEO.eq(video))
                    .selectFirst(objectContext);

            return timecode != null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


}
