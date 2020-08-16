package com.thelak.video.services;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbVideo;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.video.interfaces.IVideoService;
import com.thelak.route.video.models.VideoCreateRequest;
import com.thelak.route.video.models.VideoModel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService extends AbstractMicroservice implements IVideoService {

    @Autowired
    private DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(VideoService.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    public VideoModel get(Long id) throws MicroServiceException {
        try {

            DbVideo dbVideo = SelectById.query(DbVideo.class, id).selectFirst(objectContext);

            return VideoModel.builder()
                    .id((Long) dbVideo.getObjectId().getIdSnapshot().get("id"))
                    .title(dbVideo.getTitle())
                    .description(dbVideo.getDescription())
                    .year(dbVideo.getYear())
                    .country(dbVideo.getCountry())
                    .category(dbVideo.getCategory())
                    .duration(dbVideo.getDuration())
                    .speaker(dbVideo.getSpeaker())
                    .speakerInformation(dbVideo.getSpeakerInformation())
                    .contentUrl(dbVideo.getContentUrl())
                    .partnerLogoUrl(dbVideo.getPartnerLogoUrl())
                    .coverUrl(dbVideo.getCoverUrl())
                    .build();
        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while get video");
        }
    }

    @Override
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
                        .category(dbVideo.getCategory())
                        .duration(dbVideo.getDuration())
                        .speaker(dbVideo.getSpeaker())
                        .speakerInformation(dbVideo.getSpeakerInformation())
                        .contentUrl(dbVideo.getContentUrl())
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
    public VideoModel create(VideoCreateRequest request) throws MicroServiceException {
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
            dbVideo.setContentUrl(request.getContentUrl());
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
                    .category(dbVideo.getCategory())
                    .duration(dbVideo.getDuration())
                    .speaker(dbVideo.getSpeaker())
                    .speakerInformation(dbVideo.getSpeakerInformation())
                    .contentUrl(dbVideo.getContentUrl())
                    .partnerLogoUrl(dbVideo.getPartnerLogoUrl())
                    .coverUrl(dbVideo.getCoverUrl())
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while create video: " + e.getLocalizedMessage());
        }
    }

    @Override
    public VideoModel update(VideoModel request) throws MicroServiceException {
        try {

            DbVideo dbVideo = SelectById.query(DbVideo.class, request.getId()).selectFirst(objectContext);

            dbVideo.setTitle(request.getTitle());
            dbVideo.setDescription(request.getDescription());
            dbVideo.setYear(request.getYear());
            dbVideo.setCountry(request.getCountry());
            dbVideo.setDuration(request.getDuration());
            dbVideo.setSpeaker(request.getSpeaker());
            dbVideo.setSpeakerInformation(request.getSpeakerInformation());
            dbVideo.setContentUrl(request.getContentUrl());
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
                    .category(dbVideo.getCategory())
                    .duration(dbVideo.getDuration())
                    .speaker(dbVideo.getSpeaker())
                    .speakerInformation(dbVideo.getSpeakerInformation())
                    .contentUrl(dbVideo.getContentUrl())
                    .partnerLogoUrl(dbVideo.getPartnerLogoUrl())
                    .coverUrl(dbVideo.getCoverUrl())
                    .build();

        } catch (Exception e) {
            throw new MsInternalErrorException("Exception while updating video");
        }
    }

    @Override
    public Boolean delete(Long id) throws MicroServiceException {
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
