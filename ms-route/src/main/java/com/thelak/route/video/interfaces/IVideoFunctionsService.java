package com.thelak.route.video.interfaces;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.video.models.VideoModel;

import java.util.List;

public interface IVideoFunctionsService {

    String VIDEO_FAVORITES_ADD = "/v1/video/favorites/add";
    String VIDEO_FAVORITES_LIST = "/v1/video/favorites/list";
    String VIDEO_FAVORITES_CHECK = "/v1/video/favorites/check";
    String VIDEO_FAVORITES_DELETE = "/v1/video/favorites/delete";

    String VIDEO_HISTORY_ADD = "/v1/video/history/add";
    String VIDEO_HISTORY_LIST = "/v1/video/history/list";

    String VIDEO_TIMECODE_ADD = "/v1/video/timecode/add";
    String VIDEO_TIMECODE_GET = "/v1/video/timecode/get";

    String VIDEO_RATING_ADD = "/v1/video/rating/add";
    String VIDEO_RATING_DELETE = "/v1/video/rating/delete";
    String VIDEO_RATING_CHECK = "/v1/video/rating/check";

    Boolean addFavorites(Long videoId) throws MicroServiceException;

    List<VideoModel> listFavorites() throws MicroServiceException;

    Boolean checkFavorites(Long videoId) throws MicroServiceException;

    Boolean deleteFavorites(Long videoId) throws MicroServiceException;

    Boolean addHistory(Long videoId) throws MicroServiceException;

    List<VideoModel> listHistory() throws MicroServiceException;

    Boolean addTimeCode(Long videoId, String timecode) throws MicroServiceException;

    String getTimeCode(Long videoId) throws MicroServiceException;

    Boolean addRating(Long videoId, Integer rating) throws MicroServiceException;

    Boolean deleteRating(Long videoId) throws MicroServiceException;

    Boolean checkRating(Long videoId) throws MicroServiceException;

}
