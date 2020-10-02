package com.thelak.event.services;

import com.thelak.database.entity.DbEvent;
import com.thelak.route.event.models.EventModel;

public class EventHelper {

    public static EventModel buildEventModel(DbEvent dbEvent) {
        return EventModel.builder()
                .id((Long) dbEvent.getObjectId().getIdSnapshot().get("id"))
                .title(dbEvent.getTitle())
                .description(dbEvent.getDescription())
                .startDate(dbEvent.getStartDate())
                .endDate(dbEvent.getEndDate())
                .content(dbEvent.getContent())
                .coverUrl(dbEvent.getCoverUrl())
                .createdDate(dbEvent.getCreatedDate())
                .build();
    }
}
