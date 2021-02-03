package com.thelak.route.video.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.video.enums.VideoSortEnum;
import com.thelak.route.video.enums.VideoSortTypeEnum;
import com.thelak.route.video.models.VideoCreateRequest;
import com.thelak.route.video.models.VideoFilterModel;
import com.thelak.route.video.models.VideoModel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface IVideoService {

    String VIDEO_GET = "/v1/video/get";
    String VIDEO_GET_IDS = "/v1/video/get/ids";
    String VIDEO_LIST = "/v1/video/list";
    String VIDEO_SLIDER = "/v1/video/slider";
    String VIDEO_CREATE = "/v1/video/create";
    String VIDEO_UPDATE = "/v1/video/update";
    String VIDEO_DELETE = "/v1/video/delete";
    String VIDEO_SEARCH = "/v1/video/search";
    String VIDEO_FILTER_GET = "/v1/video/filter";

    String VIDEO_STAT_VIEWS = "/v1/video/stat/views";
    String VIDEO_STAT_LAST = "/v1/video/stat/last";

    VideoModel get(Long id) throws MicroServiceException;

    List<VideoModel> getByIds(List<Long> ids) throws MicroServiceException;

    List<VideoModel> getSliderVideos() throws MicroServiceException;

    List<VideoModel> list(Integer page, Integer size, VideoSortEnum sort, VideoSortTypeEnum sortType,
                          List<String> countryFilter, List<Integer> yearFilter,
                          List<String> playgroundFilter, List<String> languageFilter,
                          List<Long> categoryFilter, List<Long> speakerFilter) throws MicroServiceException;

    List<VideoModel> search(String search, Integer page, Integer size) throws MicroServiceException;

    VideoModel create(VideoCreateRequest request) throws MicroServiceException;

    VideoModel update(VideoModel request) throws MicroServiceException;

    Boolean delete(Long id) throws MicroServiceException;

    VideoFilterModel getFilters() throws MicroServiceException;

    HashMap<String, Integer> getViewCount(List<Long> ids) throws MicroServiceException;

    HashMap<String, String> getLastView(List<Long> ids) throws MicroServiceException;
}
