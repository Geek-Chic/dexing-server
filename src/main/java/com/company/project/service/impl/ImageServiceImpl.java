package com.company.project.service.impl;

import com.company.project.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by evil on 10/13/17.
 */
@Service
public class ImageServiceImpl {
    public String upload(Map<String, MultipartFile> files){
        // 检查是否包含文件信息
        if (null == files || files.size() == 0) {
            System.out.println("图片上传异常");
        }

        String uploadUrl="http://10.0.0.7:4869";
        // 保存文件至服务器指定路径
        String fileName = null;
        for (String key : files.keySet()) {
            MultipartFile file = files.get(key);
            fileName = file.getOriginalFilename();

            String fileType = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());

            byte[] imgData = null;
            try {
                imgData = file.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String retJson = sendPost(uploadUrl, imgData, fileType);
            //json解析有很多框架，所以不做参考了
            System.out.println(retJson);
            return retJson;
        }
        return null;
    }

    public static byte[] imageBinary() {
        File f = new File("E://7.jpg");
        BufferedImage bi;
        try {
            bi = ImageIO.read(f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url  发送请求的 URL
     * @param PostData 请求参数，请求参数应该是 byte[] 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, byte[] PostData, String fileType) {
        OutputStream outStream = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", fileType);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //二进制
            outStream = conn.getOutputStream();
            outStream.write(PostData);
            outStream.flush();

            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    private static String macthContentType(String fileType){
        String contentType = "image/jpeg";
        switch (fileType) {
            case "bmp":
                contentType = "application/x-bmp";
                break;
            case "img":
                contentType = "application/x-img";
                break;
            case "jpeg":
                contentType = "image/jpeg";
                break;
            case "jpg":
                contentType = "application/x-jpg";
                break;
            case "png":
                contentType = "image/png";
                break;
            default:
                break;
        }
        return contentType;
    }
}
