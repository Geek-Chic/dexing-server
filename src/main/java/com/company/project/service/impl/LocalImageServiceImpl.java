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
                    file.transferTo(new File(imagePath));
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
        ImagePlus imagePlus = IJ.openImage(path);
        BufferedImage bufferedImage = imagePlus.getBufferedImage();
        if (null != imageHandleData.getHeight() || null != imageHandleData.getWidth()) {
            try {
                int width = null != imageHandleData.getWidth() ? imageHandleData.getWidth() : imagePlus.getWidth();
                int height = null != imageHandleData.getHeight() ? imageHandleData.getHeight() : imagePlus.getHeight();
                bufferedImage = cropAndResize(imagePlus, width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bufferedImage;
//        InputStream is = null;
//        try {
//            is = new FileInputStream(path);
//            BufferedImage img = ImageIO.read(is);
//            return img;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public BufferedImage cropAndResize(ImagePlus imp, int targetWidth, int targetHeight) throws Exception {
        ImageProcessor ip = imp.getProcessor();
        ip.setInterpolationMethod(ImageProcessor.BILINEAR);
        ip = ip.resize(targetWidth * 2, targetHeight * 2);

        int cropX = ip.getWidth() / 2;
        int cropY = ip.getHeight() / 2;
        ip.setRoi(cropX, cropY, targetWidth, targetHeight);
        ImageProcessor cropped = ip.crop();
        BufferedImage croppedImage = ip.getBufferedImage();
//        new ImagePlus("croppedImage", croppedImage).show();
//        String thumbFilePaht=Paths.get(uploadImageRoot,"thumbnail",).toString();
//        ImageIO.write(croppedImage, "jpg", new File("cropped.jpg"));
        return croppedImage;
    }
}
