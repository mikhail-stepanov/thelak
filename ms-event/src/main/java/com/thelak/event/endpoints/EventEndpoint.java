package com.thelak.event.endpoints;

import com.thelak.core.endpoints.AbstractMicroservice;
import com.thelak.database.DatabaseService;
import com.thelak.database.entity.DbEvent;
import com.thelak.database.entity.DbVideo;
import com.thelak.route.event.interfaces.IEventService;
import com.thelak.route.event.models.EventCreateModel;
import com.thelak.route.event.models.EventModel;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.exceptions.MsInternalErrorException;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.thelak.event.services.EventHelper.buildEventModel;

@RestController
@Api(value = "Event API", produces = "application/json")
public class EventEndpoint extends AbstractMicroservice implements IEventService {

    @Autowired
    private DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(EventEndpoint.class);

    @PostConstruct
    private void initialize() {
        objectContext = databaseService.getContext();
    }

    @Override
    @ApiOperation(value = "Get event by id")
    @RequestMapping(value = EVENT_GET, method = {RequestMethod.GET})
    public EventModel get(@RequestParam Long id) throws MicroServiceException {
        try {
            DbEvent dbEvent = SelectById.query(DbEvent.class, id).selectFirst(objectContext);

            return buildEventModel(dbEvent);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of events by ids")
    @RequestMapping(value = EVENT_GET_IDS, method = {RequestMethod.GET})
    public List<EventModel> getByIds(@RequestParam List<Long> ids) throws MicroServiceException {
        try {
            List<DbEvent> dbEvents;
            dbEvents = ObjectSelect.query(DbEvent.class).
                    where(ExpressionFactory.inDbExp(DbEvent.ID_PK_COLUMN, ids))
                    .select(objectContext);

            List<EventModel> eventModels = new ArrayList<>();

            dbEvents.forEach(dbEvent -> {
                eventModels.add(buildEventModel(dbEvent));
            });

            return eventModels;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Get list of events")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "page",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "size",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "startDate",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "endDate",
                    paramType = "query")
    })
    @RequestMapping(value = EVENT_LIST, method = {RequestMethod.GET})
    public List<EventModel> list(@RequestParam(required = false) Integer page,
                                 @RequestParam(required = false) Integer size,
                                 @RequestParam(required = false) LocalDate startDate,
                                 @RequestParam(required = false) LocalDate endDate) throws MicroServiceException {
        try {

            final Expression startDateExpression;
            if (startDate != null)
                startDateExpression = DbEvent.DATE.gte(startDate);
            else startDateExpression = DbVideo.TITLE.isNotNull();

            final Expression endDateExpression;
            if (endDate != null)
                endDateExpression = DbEvent.DATE.lte(endDate);
            else endDateExpression = DbVideo.TITLE.isNotNull();

            List<DbEvent> dbEvents;
            if (page == null || size == null)
                dbEvents = ObjectSelect.query(DbEvent.class)
                        .where(startDateExpression)
                        .and(endDateExpression)
                        .pageSize(30)
                        .select(objectContext);
            else {
                dbEvents = ObjectSelect.query(DbEvent.class)
                        .where(startDateExpression)
                        .and(endDateExpression)
                        .pageSize(size)
                        .select(objectContext);
                dbEvents = dbEvents.subList(page * size - size, page * size);
            }

            List<EventModel> eventModels = new ArrayList<>();

            dbEvents.forEach(dbEvent -> {
                eventModels.add(buildEventModel(dbEvent));
            });

            return eventModels;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Find event by title/description")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "page",
                    paramType = "query"),
            @ApiImplicitParam(
                    name = "size",
                    paramType = "query")})
    @RequestMapping(value = EVENT_SEARCH, method = {RequestMethod.GET})
    public List<EventModel> search(@RequestParam String search,
                                   @RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer size) throws MicroServiceException {
        try {

            List<DbEvent> dbEvents;
            if (page == null || size == null)
                dbEvents = ObjectSelect.query(DbEvent.class).
                        where(DbEvent.DELETED_DATE.isNull())
                        .and(DbEvent.DESCRIPTION.containsIgnoreCase(search.toLowerCase()))
                        .or(DbEvent.TITLE.containsIgnoreCase(search.toLowerCase()))
                        .pageSize(30)
                        .select(objectContext);
            else {
                dbEvents = ObjectSelect.query(DbEvent.class).
                        where(DbEvent.DELETED_DATE.isNull())
                        .and(DbEvent.DESCRIPTION.containsIgnoreCase(search.toLowerCase()))
                        .or(DbEvent.TITLE.containsIgnoreCase(search.toLowerCase()))
                        .pageSize(size)
                        .select(objectContext);
                dbEvents = dbEvents.subList(page * size - size, page * size);
            }

            List<EventModel> eventModels = new ArrayList<>();

            dbEvents.forEach(dbEvent -> {
                eventModels.add(buildEventModel(dbEvent));
            });

            return eventModels;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Create event")
    @RequestMapping(value = EVENT_CREATE, method = {RequestMethod.POST})
    public EventModel create(@RequestBody EventCreateModel request) throws MicroServiceException {
        try {

            DbEvent dbEvent = objectContext.newObject(DbEvent.class);
            dbEvent.setTitle(request.getTitle());
            dbEvent.setDescription(request.getDescription());
            dbEvent.setContent(request.getContent());
            dbEvent.setDate(request.getDate());
            dbEvent.setCreatedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return buildEventModel(dbEvent);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Update event by id")
    @RequestMapping(value = EVENT_UPDATE, method = {RequestMethod.PUT})
    public EventModel update(@RequestBody EventModel request) throws MicroServiceException {
        try {

            DbEvent dbEvent = SelectById.query(DbEvent.class, request.getId()).selectFirst(objectContext);

            dbEvent.setTitle(request.getTitle());
            dbEvent.setDescription(request.getDescription());
            dbEvent.setContent(request.getContent());
            dbEvent.setDate(request.getDate());
            dbEvent.setModifiedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return buildEventModel(dbEvent);

        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }

    @Override
    @ApiOperation(value = "Delete event by id")
    @RequestMapping(value = EVENT_DELETE, method = {RequestMethod.DELETE})
    public Boolean delete(@RequestParam Long id) throws MicroServiceException {
        try {
            DbEvent dbEvent = SelectById.query(DbEvent.class, id).selectFirst(objectContext);

            dbEvent.setDeletedDate(LocalDateTime.now());

            objectContext.commitChanges();

            return true;
        } catch (Exception e) {
            throw new MsInternalErrorException(e.getMessage());
        }
    }
}
