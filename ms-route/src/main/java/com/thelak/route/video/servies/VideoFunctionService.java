package com.thelak.route.video.servies;

import com.thelak.route.common.services.BaseMicroservice;
import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.video.interfaces.IVideoFunctionsService;
import com.thelak.route.video.models.VideoModel;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class VideoFunctionService extends BaseMicroservice implements IVideoFunctionsService {

    public VideoFunctionService(RestTemplate restTemplate) {
        super("ms-video", restTemplate);
    }

    @Override
    public Boolean addFavorites(Long videoId) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_FAVORITES_ADD), videoId, Boolean.class).getBody());
    }

    @Override
    public List<VideoModel> listFavorites() throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_FAVORITES_LIST), null, List.class).getBody());
    }

    @Override
    public Boolean checkFavorites(Long videoId) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_FAVORITES_CHECK), videoId, Boolean.class).getBody());
    }

    @Override
    public Boolean deleteFavorites(Long videoId) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_FAVORITES_DELETE), videoId, Boolean.class).getBody());
    }

    @Override
    public Boolean addHistory(Long videoId) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_HISTORY_ADD), videoId, Boolean.class).getBody());
    }

    @Override
    public List<VideoModel> listHistory() throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_HISTORY_LIST), null, List.class).getBody());
    }

    @Override
    public Boolean addTimeCode(Long videoId, String timecode) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_TIMECODE_ADD), videoId, Boolean.class).getBody());
    }

    @Override
    public String getTimeCode(Long videoId) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_TIMECODE_GET), videoId, String.class).getBody());
    }

    @Override
    public Boolean addRating(Long videoId, Integer rating) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_RATING_ADD), videoId, Boolean.class).getBody());
    }

    @Override
    public Boolean deleteRating(Long videoId) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_RATING_DELETE), videoId, Boolean.class).getBody());
    }

    @Override
    public Boolean checkRating(Long videoId) throws MicroServiceException {
        return retry(() -> restTemplate.postForEntity(buildUrl(VIDEO_RATING_DELETE), videoId, Boolean.class).getBody());
    }
}
