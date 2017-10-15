package com.company.project.web;

import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.service.ImageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by evil on 10/15/17.
 */

@RestController
@RequestMapping("/image")
public class UploadController {
    @Resource
    private ImageService localImageServiceImpl;

    @RequestMapping(value = {"/upload"}, method = RequestMethod.POST)
    public Result uploadFile(@RequestParam(required = true) MultipartFile file) {
        Map<String, MultipartFile> fileMap = new HashMap<>();
        fileMap.put("image", file);
        String res = localImageServiceImpl.upload(fileMap);
        return ResultGenerator.genSuccessResult(res);
    }
}
