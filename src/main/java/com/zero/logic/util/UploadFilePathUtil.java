package com.zero.logic.util;

import java.util.Properties;

/**
 * 获取文件上传路径
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/12
 */
public class UploadFilePathUtil {
    public static String uploadFilePath;//文件保存路径
    public static String uploadFileSize;//文件大小设置

    static {
        try {
            Properties propes = ReadProperties.getPropes("/application.properties");
            uploadFilePath = propes.getProperty("uploadFilePath");
            uploadFileSize = propes.getProperty("uploadFileSize");

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
