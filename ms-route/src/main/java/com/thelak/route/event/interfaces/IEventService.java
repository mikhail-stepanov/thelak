package com.thelak.route.event.interfaces;

import com.thelak.route.event.models.EventCreateModel;
import com.thelak.route.event.models.EventModel;
import com.thelak.route.exceptions.MicroServiceException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public interface IEventService {

    String EVENT_GET = "/v1/event/get";
    String EVENT_GET_IDS = "/v1/event/get/ids";
    String EVENT_LIST = "/v1/event/list";
    String EVENT_CREATE = "/v1/event/create";
    String EVENT_UPDATE = "/v1/event/update";
    String EVENT_DELETE = "/v1/event/delete";
    String EVENT_SEARCH = "/v1/event/search";

    EventModel get(Long id) throws MicroServiceException;

    List<EventModel> getByIds(List<Long> ids) throws MicroServiceException;

    List<EventModel> list(Integer page, Integer size, String startDate, String endDate) throws MicroServiceException;

    List<EventModel> search(String search, Integer page, Integer size) throws MicroServiceException;

    EventModel create(EventCreateModel request) throws MicroServiceException;

    EventModel update(EventModel request) throws MicroServiceException;

    Boolean delete(Long id) throws MicroServiceException;
}
