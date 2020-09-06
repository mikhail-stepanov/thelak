package com.thelak.route.speaker.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.speaker.models.SpeakerCreateRequest;
import com.thelak.route.speaker.models.SpeakerModel;

import java.util.List;

public interface ISpeakerService {

    String SPEAKER_GET = "/v1/speaker/get";
    String SPEAKER_GET_IDS = "/v1/speaker/get/ids";
    String SPEAKER_LIST = "/v1/speaker/list";
    String SPEAKER_CREATE = "/v1/speaker/create";
    String SPEAKER_UPDATE = "/v1/speaker/update";
    String SPEAKER_DELETE = "/v1/speaker/delete";
    String SPEAKER_SEARCH = "/v1/speaker/search";

    SpeakerModel get(Long id) throws MicroServiceException;

    List<SpeakerModel> getByIds(List<Long> ids) throws MicroServiceException;

    List<SpeakerModel> list(Integer page, Integer size,
                            List<String> countryFilter) throws MicroServiceException;

    List<SpeakerModel> search(String search, Integer page, Integer size) throws MicroServiceException;

    SpeakerModel create(SpeakerCreateRequest request) throws MicroServiceException;

    SpeakerModel update(SpeakerModel request) throws MicroServiceException;

    Boolean delete(Long id) throws MicroServiceException;
}
