package com.thelak.video.endpoints;

import com.thelak.route.exceptions.MicroServiceException;
import com.thelak.route.video.interfaces.IVideoService;
import com.thelak.route.video.models.VideoCreateRequest;
import com.thelak.route.video.models.VideoModel;
import com.thelak.video.services.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "Video API", produces = "application/json")
public class VideoEndpoint implements IVideoService {

    @Autowired
    private VideoService videoService;

    @Override
    @ApiOperation(value = "Get video by id")
    @RequestMapping(value = VIDEO_GET, method = {RequestMethod.GET})
    public VideoModel get(@RequestParam Long id) throws MicroServiceException {
        return videoService.get(id);
    }

    @Override
    @ApiOperation(value = "Get list of videos")
    @RequestMapping(value = VIDEO_LIST, method = {RequestMethod.GET})
    public List<VideoModel> list() throws MicroServiceException {
        return videoService.list();
    }

    @Override
    @ApiOperation(value = "Find videos by title/description/speaker")
    @RequestMapping(value = VIDEO_SEARCH, method = {RequestMethod.GET})
    public List<VideoModel> search(@RequestParam String search) throws MicroServiceException {
        return videoService.search(search);
    }

    @Override
    @ApiOperation(value = "Create video")
    @RequestMapping(value = VIDEO_CREATE, method = {RequestMethod.POST})
    public VideoModel create(@RequestBody VideoCreateRequest request) throws MicroServiceException {
        return videoService.create(request);
    }

    @Override
    @ApiOperation(value = "Update video by id")
    @RequestMapping(value = VIDEO_UPDATE, method = {RequestMethod.POST})
    public VideoModel update(@RequestBody VideoModel request) throws MicroServiceException {
        return videoService.update(request);
    }

    @Override
    @ApiOperation(value = "Delete video by id")
    @RequestMapping(value = VIDEO_DELETE, method = {RequestMethod.DELETE})
    public Boolean delete(@RequestParam Long id) throws MicroServiceException {
        return videoService.delete(id);
    }
}
