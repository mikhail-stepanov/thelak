package com.thelak.speaker.services;

import com.thelak.database.entity.DbSpeaker;
import com.thelak.route.speaker.models.SpeakerModel;

public class SpeakerHelper {

    public static SpeakerModel buildSpeakerModel(DbSpeaker dbSpeaker) {
        return SpeakerModel.builder()
                .id((Long) dbSpeaker.getObjectId().getIdSnapshot().get("id"))
                .name(dbSpeaker.getName())
                .shortDescription(dbSpeaker.getShortDescription())
                .description(dbSpeaker.getDescription())
                .country(dbSpeaker.getCountry())
                .countryFlagCode(dbSpeaker.getCountryFlagCode())
                .photoUrl(dbSpeaker.getPhotoUrl())
                .createdDate(dbSpeaker.getCreatedDate())
                .build();
    }
}
