package com.company.project.service.impl;

import com.company.project.dto.ImageHandleData;
import com.company.project.service.ImageService;
import com.company.project.utils.ImgCompressUtil;
import com.company.project.utils.MD5Util;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by evil on 10/15/17.
 */
@Service
@Slf4j
public class LocalImageServiceImpl implements ImageService {

    @Value("${upload.image.root}")
    private String uploadImageRoot;
    private static final int THREAD_POOL_SIZE = 8;

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
//                    try {
//                        getThumbnail(trueFileName, 150, 88);
//                        getThumbnail(trueFileName, 70, 70);
//                        getThumbnail(trueFileName, 280, 165);
//                        getThumbnail(trueFileName, 750, 440);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    ImgCompressUtil.tosmallerpic(bImg, imagePath, 150, 88, (float) 0.85);
//                    ImgCompressUtil.tosmallerpic(bImg, imagePath, 70, 70, (float) 0.85);
//                    ImgCompressUtil.tosmallerpic(bImg, imagePath, 750, 440, (float) 0.85);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(4,
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build());
        final String finalTrueFileName = trueFileName;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    getThumbnail(finalTrueFileName, 150, 88);
                    getThumbnail(finalTrueFileName, 70, 70);
                    getThumbnail(finalTrueFileName, 280, 165);
                    getThumbnail(finalTrueFileName, 750, 440);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
            log.info("get thumbnail:" + thumbnailPath);
            return ImageIO.read(thumbnailFile);
        }
        log.info("get orgFile:" + path);
        ImagePlus imp = IJ.openImage(path);
        if (null != imp) {
            BufferedImage resizeImage = cropAndResize(imp, targetWidth, targetHeight);
            ImageIO.write(resizeImage, "jpg", new File(thumbnailPath));
            return resizeImage;
        }
        return null;
    }
}
