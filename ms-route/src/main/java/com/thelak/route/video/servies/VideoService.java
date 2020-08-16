package com.thelak.route.video.servies;

import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.video.interfaces.IVideoService;
import com.thelak.route.video.models.VideoCreateRequest;
import com.thelak.route.video.models.VideoModel;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class VideoService extends BaseMicroservice implements IVideoService {

    public VideoService(RestTemplate restTemplate) {
        super("ms-video", restTemplate);
    }

    @Override
    public VideoModel get(Long id) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_GET), id, VideoModel.class).getBody());
    }

    @Override
    public List<VideoModel> list() throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_LIST), null, List.class).getBody());
    }

    @Override
    public VideoModel create(VideoCreateRequest request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_CREATE), request, VideoModel.class).getBody());
    }

    @Override
    public VideoModel update(VideoModel request) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_UPDATE), request, VideoModel.class).getBody());
    }

    @Override
    public Boolean delete(Long id) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_DELETE), id, Boolean.class).getBody());
    }
}
