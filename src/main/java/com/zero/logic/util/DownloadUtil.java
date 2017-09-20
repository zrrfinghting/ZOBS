package com.zero.logic.util;/**
 * Created by Admin on 2017/6/14.
 */

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 下载工具类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/14
 */
public class DownloadUtil {

    public static void downloadImg(String path, HttpServletResponse response){
        InputStream in =null;
        OutputStream out = null;

        try {
            response.setContentType("image/jpeg");
            in = new FileInputStream(path);
            int len = 0;
            byte [] buffer = new byte[1024];
            out = response.getOutputStream();
            while ((len=in.read(buffer))>0){
                out.write(buffer,0,len);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closeSilently(out);
            closeSilently(in);
        }
    }

    /**
     * 关闭资源
     * @param closeable
     * @return
     */
    private static Closeable closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException exception) {

        }
        return null;
    }
}
