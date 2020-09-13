package com.thelak.route.speaker.interfaces;

import com.thelak.route.exceptions.MicroServiceException;

import java.util.List;

public interface ISpeakerContentService {

    String SPEAKER_VIDEO = "/v1/speaker/content/video";
    String SPEAKER_ARTICLE = "/v1/speaker/content/article";
    String SPEAKER_EVENT = "/v1/speaker/content/event";

    List<Long> videoIds(List<Long> speakerIds) throws MicroServiceException;

    List<Long> articleIds(List<Long> speakerIds) throws MicroServiceException;

    List<Long> eventIds(List<Long> speakerIds) throws MicroServiceException;
}
