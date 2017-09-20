package com.zero.logic.controller;

import com.zero.logic.util.DownloadUtil;
import com.zero.logic.util.JsonUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 下载图片
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/15
 */

@RestController
@RequestMapping("util")
public class UtilController {
    @RequestMapping(value = "downloadImg",method = RequestMethod.GET)
    @ApiOperation(value = "下载图片",notes = "下载图片")
    public String img(@RequestParam("fileName") String fileName,HttpServletResponse response){
        try {
            DownloadUtil.downloadImg(System.getProperty("user.dir")+ File.separator+"upload"
                    +File.separator+"bookImg"+File.separator+fileName,response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"图片下载成功");
    }
}
