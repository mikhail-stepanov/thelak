package com.thelak.route.speaker.services;

import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.speaker.interfaces.ISpeakerContentService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class SpeakerContentService extends BaseMicroservice implements ISpeakerContentService {

    public SpeakerContentService(RestTemplate restTemplate) {
        super("ms-speaker", restTemplate);
    }

    @Override
    public List<Long> videoIds(List<Long> speakerIds) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(SPEAKER_VIDEO))
                .queryParam("speakerIds", speakerIds.toArray());
        return retry(() -> restTemplate.getForEntity(builder.toUriString(), List.class, speakerIds).getBody());
    }

    @Override
    public List<Long> articleIds(List<Long> speakerIds) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(SPEAKER_ARTICLE))
                .queryParam("speakerIds", speakerIds.toArray());

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), List.class, speakerIds).getBody());
    }

    @Override
    public List<Long> eventIds(List<Long> speakerIds) throws MicroServiceException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildUrl(SPEAKER_EVENT))
                .queryParam("speakerIds", speakerIds.toArray());

        return retry(() -> restTemplate.getForEntity(builder.toUriString(), List.class, speakerIds).getBody());
    }
}
