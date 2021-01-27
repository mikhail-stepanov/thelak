package com.thelak.route.event.services;

import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.event.interfaces.IEventService;
import com.thelak.route.event.models.EventCreateModel;
import com.thelak.route.event.models.EventModel;
import com.thelak.route.exceptions.MicroServiceException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public class EventService extends BaseMicroservice implements IEventService {

    public EventService(RestTemplate restTemplate) {
        super("ms-event", restTemplate);
    }

    @Override
    public EventModel get(Long id) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(EVENT_GET), EventModel.class, id).getBody());
    }

    @Override
    public List<EventModel> getByIds(List<Long> ids) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(EVENT_GET_IDS), List.class, ids).getBody());
    }

    @Override
    public List<EventModel> list(Integer page, Integer size, ZonedDateTime startDate, ZonedDateTime endDate) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(EVENT_LIST), List.class,
                page, size).getBody());
    }

    @Override
    public List<EventModel> search(String search, Integer page, Integer size) throws MicroServiceException {
        return retry(() -> restTemplate.getForEntity(buildUrl(EVENT_SEARCH), List.class, search, page, size).getBody());
    }

    @Override
    public EventModel create(EventCreateModel request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(EVENT_CREATE), request, EventModel.class).getBody());
    }

    @Override
    public EventModel update(EventModel request) throws MicroServiceException {
        return null;
    }

    @Override
    public Boolean delete(Long id) throws MicroServiceException {
        return false;
    }
}
