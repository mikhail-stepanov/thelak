package com.thelak.route.video.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.video.enums.VideoSortEnum;
import com.thelak.route.video.enums.VideoSortTypeEnum;
import com.thelak.route.video.models.VideoCreateRequest;
import com.thelak.route.video.models.VideoFilterModel;
import com.thelak.route.video.models.VideoModel;

import java.util.List;

public interface IVideoService {

    String VIDEO_GET = "/v1/video/get";
    String VIDEO_GET_IDS = "/v1/video/get/ids";
    String VIDEO_LIST = "/v1/video/list";
    String VIDEO_CREATE = "/v1/video/create";
    String VIDEO_UPDATE = "/v1/video/update";
    String VIDEO_DELETE = "/v1/video/delete";
    String VIDEO_SEARCH = "/v1/video/search";
    String VIDEO_FILTER_GET = "/v1/video/filter";


    VideoModel get(Long id) throws MicroServiceException;

    List<VideoModel> getByIds(List<Long> ids) throws MicroServiceException;

    List<VideoModel> list(Integer page, Integer size, VideoSortEnum sort, VideoSortTypeEnum sortType,
                          List<String> countryFilter, List<Integer> yearFilter,
                          List<String> playgroundFilter, List<String> languageFilter) throws MicroServiceException;

    List<VideoModel> search(String search, Integer page, Integer size) throws MicroServiceException;

    VideoModel create(VideoCreateRequest request) throws MicroServiceException;

    VideoModel update(VideoModel request) throws MicroServiceException;

    Boolean delete(Long id) throws MicroServiceException;

    VideoFilterModel getFilters() throws MicroServiceException;
}
