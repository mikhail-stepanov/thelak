package com.thelak.speaker.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.*;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.speaker.interfaces.ISpeakerService;
import com.thelak.route.speaker.models.SpeakerCreateRequest;
import com.thelak.route.speaker.models.SpeakerModel;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.thelak.speaker.services.SpeakerHelper.buildSpeakerModel;

@RestController
@Api(value = "Speaker API", produces = "application/json")
public class SpeakerEndpoint extends AbstractMicroservice implements ISpeakerService {

    @Autowired
    private DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(SpeakerEndpoint.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    @ApiOperation(value = "Get speaker by id")
    @RequestMapping(value = SPEAKER_GET, method = {RequestMethod.GET})
    public SpeakerModel get(@RequestParam Long id) throws MicroServiceException {
        try {

            DbSpeaker dbSpeaker = SelectById.query(DbSpeaker.class, id).selectFirst(objectContext);

            return buildSpeakerModel(dbSpeaker);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of speaker by ids")
    @RequestMapping(value = SPEAKER_GET_IDS, method = {RequestMethod.GET})
    public List<SpeakerModel> getByIds(@RequestParam List<Long> ids) throws MicroServiceException {
        try {
            List<DbSpeaker> dbSpeakers;
            dbSpeakers = ObjectSelect.query(DbSpeaker.class).
                    where(ExpressionFactory.inDbExp(DbVideo.ID_PK_COLUMN, ids))
                    .select(objectContext);

            List<SpeakerModel> speakerModels = new ArrayList<>();

            dbSpeakers.forEach(dbSpeaker -> {
                speakerModels.add(buildSpeakerModel(dbSpeaker));
            });

            return speakerModels;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get speaker by videoId")
    @RequestMapping(value = SPEAKER_GET_VIDEO, method = {RequestMethod.GET})
    public SpeakerModel getByVideo(@RequestParam Long videoId) throws MicroServiceException {
        try {
            DbSpeakerVideos dbSpeakerVideos = ObjectSelect.query(DbSpeakerVideos.class)
                    .where(DbSpeakerVideos.ID_VIDEO.eq(videoId)).selectFirst(objectContext);

            return buildSpeakerModel(dbSpeakerVideos.getVideoToSpeaker());

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get speaker by articleId")
    @RequestMapping(value = SPEAKER_GET_ARTICLE, method = {RequestMethod.GET})
    public SpeakerModel getByArticle(@RequestParam Long articleId) throws MicroServiceException {
        try {
            DbSpeakerArticles speakerArticles = ObjectSelect.query(DbSpeakerArticles.class)
                    .where(DbSpeakerArticles.ID_ARTICLE.eq(articleId)).selectFirst(objectContext);

            return buildSpeakerModel(speakerArticles.getArticleToSpeaker());

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get speaker by eventId")
    @RequestMapping(value = SPEAKER_GET_EVENT, method = {RequestMethod.GET})
    public SpeakerModel getByEvent(@RequestParam Long eventId) throws MicroServiceException {
        try {
            DbSpeakerEvents dbSpeakerEvents = ObjectSelect.query(DbSpeakerEvents.class)
                    .where(DbSpeakerEvents.ID_EVENT.eq(eventId)).selectFirst(objectContext);

            return buildSpeakerModel(dbSpeakerEvents.getEventToSpeaker());

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of speakers")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "page",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "size",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "countryFilter",
                    paramType = "query")})
    @RequestMapping(value = SPEAKER_LIST, method = {RequestMethod.GET})
    public List<SpeakerModel> list(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer size,
                                   @RequestParam(required = false) List<String> countryFilter) throws MicroServiceException {
        try {
            final Expression countryFilterExpression;
            if (countryFilter != null)
                countryFilterExpression = DbSpeaker.COUNTRY.in(countryFilter);
            else countryFilterExpression = DbSpeaker.NAME.isNotNull();

            List<DbSpeaker> dbSpeakers;
            if (page == null || size == null)
                dbSpeakers = ObjectSelect.query(DbSpeaker.class)
                        .where(countryFilterExpression)
                        .pageSize(30)
                        .select(objectContext);
            else {
                dbSpeakers = ObjectSelect.query(DbSpeaker.class)
                        .where(countryFilterExpression)
                        .pageSize(size)
                        .select(objectContext);
                dbSpeakers = dbSpeakers.subList(page * size - size, page * size);
            }

            List<SpeakerModel> speakers = new ArrayList<>();

            dbSpeakers.forEach(dbSpeaker -> {
                speakers.add(buildSpeakerModel(dbSpeaker));
            });

            return speakers;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Find speaker by name/description")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "page",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "size",
                    paramType = "query")})
    @RequestMapping(value = SPEAKER_SEARCH, method = {RequestMethod.GET})
    public List<SpeakerModel> search(@RequestParam String search,
                                     @RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer size) throws MicroServiceException {
        try {

            List<DbSpeaker> dbSpeakers;
            if (page == null || size == null)
                dbSpeakers = ObjectSelect.query(DbSpeaker.class).
                        where(DbSpeaker.DELETED_DATE.isNull())
                        .and(DbSpeaker.DESCRIPTION.containsIgnoreCase(search.toLowerCase()))
                        .or(DbSpeaker.NAME.containsIgnoreCase(search.toLowerCase()))
                        .pageSize(30)
                        .select(objectContext);
            else {
                dbSpeakers = ObjectSelect.query(DbSpeaker.class).
                        where(DbSpeaker.DELETED_DATE.isNull())
                        .and(DbSpeaker.DESCRIPTION.containsIgnoreCase(search.toLowerCase()))
                        .or(DbSpeaker.NAME.containsIgnoreCase(search.toLowerCase()))
                        .pageSize(size)
                        .select(objectContext);
                dbSpeakers = dbSpeakers.subList(page * size - size, page * size);
            }

            List<SpeakerModel> speakers = new ArrayList<>();

            dbSpeakers.forEach(dbSpeaker -> {
                speakers.add(buildSpeakerModel(dbSpeaker));
            });

            return speakers;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Create speaker")
    @RequestMapping(value = SPEAKER_CREATE, method = {RequestMethod.POST})
    public SpeakerModel create(@RequestBody SpeakerCreateRequest request) throws MicroServiceException {
        try {

            DbSpeaker dbSpeaker = objectContext.newObject(DbSpeaker.class);
            dbSpeaker.setName(request.getName());
            dbSpeaker.setDescription(request.getDescription());
            dbSpeaker.setShortDescription(request.getShortDescription());
            dbSpeaker.setCountry(request.getCountry());
            dbSpeaker.setPhotoUrl(request.getPhotoUrl());
            dbSpeaker.setCreatedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return buildSpeakerModel(dbSpeaker);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Update speaker by id")
    @RequestMapping(value = SPEAKER_UPDATE, method = {RequestMethod.PUT})
    public SpeakerModel update(@RequestBody SpeakerModel request) throws MicroServiceException {
        try {

            DbSpeaker dbSpeaker = SelectById.query(DbSpeaker.class, request.getId()).selectFirst(objectContext);

            dbSpeaker.setName(request.getName());
            dbSpeaker.setDescription(request.getDescription());
            dbSpeaker.setShortDescription(request.getShortDescription());
            dbSpeaker.setCountry(request.getCountry());
            dbSpeaker.setPhotoUrl(request.getPhotoUrl());
            dbSpeaker.setModifiedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return buildSpeakerModel(dbSpeaker);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Delete speaker by id")
    @RequestMapping(value = SPEAKER_DELETE, method = {RequestMethod.DELETE})
    public Boolean delete(@RequestParam Long id) throws MicroServiceException {
        try {
            DbSpeaker dbSpeaker = SelectById.query(DbSpeaker.class, id).selectFirst(objectContext);

            dbSpeaker.setDeletedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }
}
