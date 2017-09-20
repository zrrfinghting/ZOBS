package com.zero.logic.controller;
import com.zero.basic.filter.BasicFilter;
import com.zero.logic.dao.LogDao;
import com.zero.logic.domain.Log;
import com.zero.logic.util.JsonUtil;

import com.zero.logic.util.UploadFileUtil;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 文件上传
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/12
 */
@RestController
@RequestMapping("upload")
public class UploadFileController {

    @Autowired
    private LogDao logDao;
   @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    @ApiOperation(value = "文件上传",notes = "文件上传")
    public String singleFileUpload(@RequestParam("file")MultipartFile file) throws JSONException {
        try {
            String msg = UploadFileUtil.singleFileUpload(file);
            int status = Integer.parseInt(JsonUtil.getString("status",msg));
            if(status==200){
                String fileName = JsonUtil.getString("msg",msg);
                logDao.save(new Log(new Date(),new Date(),"文件"+file.getOriginalFilename()+"上传成功",0, BasicFilter.user_id));
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"文件上传失败");
        }
    }
   /* @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    @ApiOperation(value = "文件上传",notes = "文件上传")
    public String multipleFileUpload(HttpServletRequest request,@RequestParam("file") MultipartFile[] files) throws JSONException {

        try {
            String msg = UploadFileUtil.moreFileUpload(files);
            if("文件上传成功".equals(msg)){
                return JsonUtil.returnStr(200,msg);
            }else {
                return JsonUtil.returnStr(500,msg);
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"文件上传失败");
        }
    }*/
}
