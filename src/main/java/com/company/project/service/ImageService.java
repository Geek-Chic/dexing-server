package com.company.project.service;

import com.company.project.dto.ImageHandleData;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Created by evil on 10/13/17.
 */
public interface ImageService {
   public String upload(Map<String, MultipartFile> files);

   public BufferedImage getBufferedImage(String md5, ImageHandleData imageHandleData);

}
