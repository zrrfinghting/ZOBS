package com.zero.logic.util;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/12
 */
public class UploadFileUtil {


    private static String fileName="";
    /**
     * 单个文件上传
     * @param file
     * @return msg
     */
    public static  String singleFileUpload(MultipartFile file) throws Exception {
        if(file.isEmpty()){
            //redirectAttributes.addFlashAttribute("message","请选择一个文件上传！");
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"请选择上传文件");
        }
        if(!scopeFileSize(file)){//上传文件大小限制
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"文件太大");
        }
        if(".exe".equals(getFileType(file.getOriginalFilename()))){
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"不能上传应用程序文件");
        }
        try {
            //获取文件、保存文件
            byte[] bytes = file.getBytes();
            //用时间戳+文件名重命名文件名
             fileName = System.currentTimeMillis()+getFileType(file.getOriginalFilename());
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(UploadFilePathUtil.uploadFilePath+"/"+fileName)));
            out.write(bytes);
            out.flush();
            out.close();
        }catch (IOException e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"文件上传失败");
        }
        Map map = new HashMap();
        map.put("status",JsonUtil.RESULT_SUCCESS);
        map.put("msg","上传成功");
        map.put("fileName",fileName);
        return JsonUtil.fromObject(map);
    }

    /**
     * 设置文件大小
     * @param file
     * @return
     */
    public static boolean scopeFileSize(MultipartFile file){
        if(UploadFilePathUtil.uploadFileSize == null){//如果用户不设置则不作限制
            return true;
        }
        long fileSize = Integer.parseInt(UploadFilePathUtil.uploadFileSize)*1024*1024;//单位为：字节
        if(file.getSize()>0 && file.getSize()<fileSize){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 获取文件名后缀
     * @param fileName
     * @return
     */
    public static String getFileType(String fileName){
        if(!"".equals(fileName) &&fileName!=null &&fileName.indexOf(".")>=0){
            return fileName.substring(fileName.lastIndexOf("."),fileName.length());
        }
        return "";
    }

    /**
     * 获取已经存在的文件列表
     * @param filePath
     * @return
     */
    public static List<String> getExistsFile(String filePath){
        File[] files = new File(filePath).listFiles();
        List<String> fileNameList = new ArrayList<>();
        for (File file:files){
            fileNameList.add(file.getName());
        }
        return  fileNameList;
    }




    /**
     * 多个文件上传
     * @param files
     * @return
     */
/*    public static String moreFileUpload(MultipartFile[] files){
        if(files!=null && files.length>=1){
            BufferedOutputStream bs = null;
            MultipartFile file = null;
            for(int i = 0;i<files.length;i++){
                file = files[i];

                if(file.isEmpty()){
                    return "第"+i+"个文件为空上传失败";
                }
                if(!file.isEmpty()){
                    try{
                        if(!scopeFileSize(file)){//上传文件大小限制
                            return  "第"+i+"个文件:"+file.getOriginalFilename()+"超过文件上传大小限制";
                        }
                        byte[] bytes = file.getBytes();
                        bs = new BufferedOutputStream(new FileOutputStream(new File(UploadFilePathUtil.uploadFilePath+"/"+file.getOriginalFilename())));
                        bs.write(bytes);
                        bs.flush();
                        bs.close();
                    }catch(IOException e){
                        e.printStackTrace();
                        return "第"+i+"个文件:"+file.getOriginalFilename()+"上传失败";
                    }
                }
            }
        }
        return  "文件上传成功";
    }*/
}
