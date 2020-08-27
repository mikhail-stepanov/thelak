package com.thelak.video.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbVideo;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.video.interfaces.IVideoService;
import com.thelak.route.video.models.VideoCreateRequest;
import com.thelak.route.video.models.VideoModel;
import com.thelak.route.video.models.VideoSourceModel;
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

import static com.thelak.video.service.VideoHelper.avgRating;
import static com.thelak.video.service.VideoHelper.createSources;

@RestController
@Api(value = "Video API", produces = "application/json")
public class VideoEndpoint extends AbstractMicroservice implements IVideoService {

    @Autowired
    private DatabaseService databaseService;

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

            DbVideo dbVideo = SelectById.query(DbVideo.class, id).selectFirst(objectContext);

            return VideoModel.builder()
                    .id((Long) dbVideo.getObjectId().getIdSnapshot().get("id"))
                    .title(dbVideo.getTitle())
                    .description(dbVideo.getDescription())
                    .year(dbVideo.getYear())
                    .country(dbVideo.getCountry())
                    .language(dbVideo.getLanguage())
                    .category(dbVideo.getCategory())
                    .duration(dbVideo.getDuration())
                    .speaker(dbVideo.getSpeaker())
                    .speakerInformation(dbVideo.getSpeakerInformation())
                    .playground(dbVideo.getPlayground())
                    .sources(createSources(dbVideo))
                    .rating(avgRating(dbVideo))
                    .partnerLogoUrl(dbVideo.getPartnerLogoUrl())
                    .coverUrl(dbVideo.getCoverUrl())
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while get video");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Get list of videos")
    @RequestMapping(value = VIDEO_LIST, method = {RequestMethod.GET})
    public List<VideoModel> list() throws MicroServiceException {
        try {

            List<DbVideo> dbVideos = ObjectSelect.query(DbVideo.class).select(objectContext);

            List<VideoModel> videos = new ArrayList<>();

            dbVideos.forEach(dbVideo -> {
                videos.add(VideoModel.builder()
                        .id((Long) dbVideo.getObjectId().getIdSnapshot().get("id"))
                        .title(dbVideo.getTitle())
                        .description(dbVideo.getDescription())
                        .year(dbVideo.getYear())
                        .country(dbVideo.getCountry())
                        .language(dbVideo.getLanguage())
                        .category(dbVideo.getCategory())
                        .duration(dbVideo.getDuration())
                        .speaker(dbVideo.getSpeaker())
                        .speakerInformation(dbVideo.getSpeakerInformation())
                        .playground(dbVideo.getPlayground())
                        .sources(createSources(dbVideo))
                        .rating(avgRating(dbVideo))
                        .partnerLogoUrl(dbVideo.getPartnerLogoUrl())
                        .coverUrl(dbVideo.getCoverUrl())
                        .build());
            });

            return videos;
        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while get list of videos");
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Find videos by title/description/speaker")
    @RequestMapping(value = VIDEO_SEARCH, method = {RequestMethod.GET})
    public List<VideoModel> search(@RequestParam String search) throws MicroServiceException {
        try {
            List<VideoModel> videos = new ArrayList<>();

            List<DbVideo> dbVideosTitle = ObjectSelect.query(DbVideo.class).
                    where(DbVideo.DELETED_DATE.isNull()).and(DbVideo.TITLE.lower().like("%" + search.toLowerCase() + "%"))
                    .limit(20)
                    .select(objectContext);

            dbVideosTitle.forEach(dbVideo -> {
                videos.add(VideoModel.builder()
                        .id((Long) dbVideo.getObjectId().getIdSnapshot().get("id"))
                        .title(dbVideo.getTitle())
                        .description(dbVideo.getDescription())
                        .year(dbVideo.getYear())
                        .country(dbVideo.getCountry())
                        .language(dbVideo.getLanguage())
                        .category(dbVideo.getCategory())
                        .duration(dbVideo.getDuration())
                        .speaker(dbVideo.getSpeaker())
                        .speakerInformation(dbVideo.getSpeakerInformation())
                        .playground(dbVideo.getPlayground())
                        .sources(createSources(dbVideo))
                        .rating(avgRating(dbVideo))
                        .partnerLogoUrl(dbVideo.getPartnerLogoUrl())
                        .coverUrl(dbVideo.getCoverUrl())
                        .build());
            });

            List<DbVideo> dbVideosDescription = ObjectSelect.query(DbVideo.class).
                    where(DbVideo.DELETED_DATE.isNull()).and(DbVideo.DESCRIPTION.lower().like("%" + search.toLowerCase() + "%"))
                    .limit(20)
                    .select(objectContext);

            dbVideosDescription.forEach(dbVideo -> {
                videos.add(VideoModel.builder()
                        .id((Long) dbVideo.getObjectId().getIdSnapshot().get("id"))
                        .title(dbVideo.getTitle())
                        .description(dbVideo.getDescription())
                        .year(dbVideo.getYear())
                        .country(dbVideo.getCountry())
                        .language(dbVideo.getLanguage())
                        .category(dbVideo.getCategory())
                        .duration(dbVideo.getDuration())
                        .speaker(dbVideo.getSpeaker())
                        .speakerInformation(dbVideo.getSpeakerInformation())
                        .playground(dbVideo.getPlayground())
                        .sources(createSources(dbVideo))
                        .rating(avgRating(dbVideo))
                        .partnerLogoUrl(dbVideo.getPartnerLogoUrl())
                        .coverUrl(dbVideo.getCoverUrl())
                        .build());
            });

            List<DbVideo> dbVideosSpeaker = ObjectSelect.query(DbVideo.class).
                    where(DbVideo.DELETED_DATE.isNull()).and(DbVideo.SPEAKER.lower().like("%" + search.toLowerCase() + "%"))
                    .limit(20)
                    .select(objectContext);

            dbVideosSpeaker.forEach(dbVideo -> {
                videos.add(VideoModel.builder()
                        .id((Long) dbVideo.getObjectId().getIdSnapshot().get("id"))
                        .title(dbVideo.getTitle())
                        .description(dbVideo.getDescription())
                        .year(dbVideo.getYear())
                        .country(dbVideo.getCountry())
                        .language(dbVideo.getLanguage())
                        .category(dbVideo.getCategory())
                        .duration(dbVideo.getDuration())
                        .speaker(dbVideo.getSpeaker())
                        .speakerInformation(dbVideo.getSpeakerInformation())
                        .playground(dbVideo.getPlayground())
                        .sources(createSources(dbVideo))
                        .rating(avgRating(dbVideo))
                        .partnerLogoUrl(dbVideo.getPartnerLogoUrl())
                        .coverUrl(dbVideo.getCoverUrl())
                        .build());
            });

            return videos;
        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while searching videos");
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

            return VideoModel.builder()
                    .id((Long) dbVideo.getObjectId().getIdSnapshot().get("id"))
                    .title(dbVideo.getTitle())
                    .description(dbVideo.getDescription())
                    .year(dbVideo.getYear())
                    .country(dbVideo.getCountry())
                    .language(dbVideo.getLanguage())
                    .category(dbVideo.getCategory())
                    .duration(dbVideo.getDuration())
                    .speaker(dbVideo.getSpeaker())
                    .speakerInformation(dbVideo.getSpeakerInformation())
                    .playground(dbVideo.getPlayground())
                    .sources(createSources(dbVideo))
                    .partnerLogoUrl(dbVideo.getPartnerLogoUrl())
                    .coverUrl(dbVideo.getCoverUrl())
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while create video: " + e.getLocalizedMessage());
        }
    }

    @Override
    @CrossOrigin
    @ApiOperation(value = "Update video by id")
    @RequestMapping(value = VIDEO_UPDATE, method = {RequestMethod.POST})
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
            dbVideo.setSpeaker(request.getSpeaker());
            dbVideo.setSpeakerInformation(request.getSpeakerInformation());
            dbVideo.setPartnerLogoUrl(request.getPartnerLogoUrl());
            dbVideo.setCoverUrl(request.getCoverUrl());
            dbVideo.setCreatedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return VideoModel.builder()
                    .id((Long) dbVideo.getObjectId().getIdSnapshot().get("id"))
                    .title(dbVideo.getTitle())
                    .description(dbVideo.getDescription())
                    .year(dbVideo.getYear())
                    .country(dbVideo.getCountry())
                    .language(dbVideo.getLanguage())
                    .category(dbVideo.getCategory())
                    .duration(dbVideo.getDuration())
                    .speaker(dbVideo.getSpeaker())
                    .speakerInformation(dbVideo.getSpeakerInformation())
                    .playground(dbVideo.getPlayground())
                    .sources(createSources(dbVideo))
                    .rating(avgRating(dbVideo))
                    .partnerLogoUrl(dbVideo.getPartnerLogoUrl())
                    .coverUrl(dbVideo.getCoverUrl())
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while updating video");
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
            throw new MsInternalErrorException("Exception while deleting video");
        }
    }
}
