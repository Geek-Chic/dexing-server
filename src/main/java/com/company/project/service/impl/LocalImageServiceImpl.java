package com.company.project.service.impl;

import com.company.project.dto.ImageHandleData;
import com.company.project.service.ImageService;
import com.company.project.utils.MD5Util;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by evil on 10/15/17.
 */
@Service
public class LocalImageServiceImpl implements ImageService {

    @Value("${upload.image.root}")
    private String uploadImageRoot;

    @Override
    public String upload(Map<String, MultipartFile> files) {
        String trueFileName = null;
        String fileType;
        for (String key : files.keySet()) {
            MultipartFile file = files.get(key);
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {
                trueFileName = MD5Util.getMD5(String.valueOf(System.currentTimeMillis()) + fileName);
                fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                String imagePath = Paths.get(uploadImageRoot, trueFileName).toString();
                try {
//                    file.transferTo(new File(imagePath));
                    BufferedImage bImg = ImageIO.read(file.getInputStream());
                    ImageIO.write(bImg, "jpg", new File(imagePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return trueFileName;
    }

    @Override
    public BufferedImage getBufferedImage(String md5, ImageHandleData imageHandleData) {
        String path = Paths.get(uploadImageRoot, md5).toString();
        if (null != imageHandleData.getHeight() || null != imageHandleData.getWidth()) {
            try {
                return getThumbnail(md5, imageHandleData.getWidth(), imageHandleData.getHeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedImage cropAndResize(ImagePlus imp, Integer targetWidth, Integer targetHeight) throws Exception {
        ImageProcessor ip = imp.getProcessor();
        targetWidth = null != targetWidth ? targetWidth : ip.getWidth();
        targetHeight = null != targetHeight ? targetHeight : ip.getHeight();
        ip.setInterpolationMethod(ImageProcessor.BILINEAR);
        ip = ip.resize(targetWidth * 2, targetHeight * 2);

        int cropX = ip.getWidth() / 2;
        int cropY = ip.getHeight() / 2;
        ip.setRoi(cropX, cropY, targetWidth, targetHeight);
        ImageProcessor cropped = ip.crop();
        BufferedImage croppedImage = ip.getBufferedImage();
        return croppedImage;
    }

    private BufferedImage getThumbnail(String md5, int targetWidth, int targetHeight) throws Exception {
        String path = Paths.get(uploadImageRoot, md5).toString();
        String thumbnailPath = Paths.get(uploadImageRoot, String.format("%s@%sx%s", md5, targetWidth, targetHeight)).toString();
        File thumbnailFile = new File(thumbnailPath);
        if (thumbnailFile.exists()) {
            return ImageIO.read(thumbnailFile);
        }
        ImagePlus imp = IJ.openImage(path);
        BufferedImage resizeImage = cropAndResize(imp, targetWidth, targetHeight);
        ImageIO.write(resizeImage, "jpg", new File(thumbnailPath));
        return resizeImage;
    }
}
