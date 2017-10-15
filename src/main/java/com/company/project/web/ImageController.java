package com.company.project.web;

import com.company.project.dto.ImageHandleData;
import com.company.project.service.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * Created by evil on 10/13/17.
 */
@Controller
@RequestMapping("/resource")
public class ImageController {
    @Resource
    private ImageService localImageServiceImpl;

    @RequestMapping(value = "/{md5}", method = RequestMethod.GET)
    public void showImage(@PathVariable String md5,
                          @RequestParam(value = "w", required = false) Integer w,
                          @RequestParam(value = "h", required = false) Integer h,
                          HttpServletResponse response) throws Exception {
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        try {
            BufferedImage image = localImageServiceImpl.getBufferedImage(md5, ImageHandleData.builder()
                    .width(w)
                    .height(h)
                    .build()
            );
            ImageIO.write(image, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        byte[] imgByte = jpegOutputStream.toByteArray();

        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpg");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(imgByte);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

}