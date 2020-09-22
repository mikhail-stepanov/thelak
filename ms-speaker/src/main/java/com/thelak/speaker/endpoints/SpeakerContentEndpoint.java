package com.thelak.speaker.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbSpeaker;
import com.thelak.database.entity.DbSpeakerArticles;
import com.thelak.database.entity.DbSpeakerEvents;
import com.thelak.database.entity.DbSpeakerVideos;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
import com.thelak.route.speaker.interfaces.ISpeakerContentService;
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
@Api(value = "Speaker content API", produces = "application/json")
public class SpeakerContentEndpoint extends AbstractMicroservice implements ISpeakerContentService {
    @Autowired
    private DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(SpeakerContentEndpoint.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    @ApiOperation(value = "Get video ids by speaker ids")
    @RequestMapping(value = SPEAKER_VIDEO, method = {RequestMethod.GET})
    public List<Long> videoIds(@RequestParam List<Long> speakerIds) throws MicroServiceException {
        try {
            List<DbSpeaker> dbSpeakers = ObjectSelect.query(DbSpeaker.class).
                    where(ExpressionFactory.inDbExp(DbSpeaker.ID_PK_COLUMN, speakerIds))
                    .select(objectContext);

            return ObjectSelect.columnQuery(DbSpeakerVideos.class, DbSpeakerVideos.ID_VIDEO)
                    .where(DbSpeakerVideos.VIDEO_TO_SPEAKER.in(dbSpeakers)).select(objectContext);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get article ids by speaker ids")
    @RequestMapping(value = SPEAKER_ARTICLE, method = {RequestMethod.GET})
    public List<Long> articleIds(List<Long> speakerIds) throws MicroServiceException {
        try {
            List<DbSpeaker> dbSpeakers = ObjectSelect.query(DbSpeaker.class).
                    where(ExpressionFactory.inDbExp(DbSpeaker.ID_PK_COLUMN, speakerIds))
                    .select(objectContext);

            return ObjectSelect.columnQuery(DbSpeakerArticles.class, DbSpeakerArticles.ID_ARTICLE)
                    .where(DbSpeakerArticles.ARTICLE_TO_SPEAKER.in(dbSpeakers)).select(objectContext);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get event ids by speaker ids")
    @RequestMapping(value = SPEAKER_EVENT, method = {RequestMethod.GET})
    public List<Long> eventIds(List<Long> speakerIds) throws MicroServiceException {
        try {
            List<DbSpeaker> dbSpeakers = ObjectSelect.query(DbSpeaker.class).
                    where(ExpressionFactory.inDbExp(DbSpeaker.ID_PK_COLUMN, speakerIds))
                    .select(objectContext);

            return ObjectSelect.columnQuery(DbSpeakerEvents.class, DbSpeakerEvents.ID_EVENT)
                    .where(DbSpeakerEvents.EVENT_TO_SPEAKER.in(dbSpeakers)).select(objectContext);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }
}
