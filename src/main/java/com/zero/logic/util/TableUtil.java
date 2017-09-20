package com.zero.logic.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 表工具类
 * @auther Deram Zhao
 * @creatTime 2017/6/2
 */
public class TableUtil {
    /**
     * 创建表格数据
     * @param total 总数
     * @param currentPage 当前页
     * @param totalPage 总页数
     * @return 分页信息
     * @throws Exception
     */
    public static String createTableDate(Object obj,long total,int currentPage,long totalPage,long pageSize){
        JSONObject json = new JSONObject();
        json.put("pageNum", currentPage);
        json.put("totalPage", totalPage);
        json.put("pageSize",pageSize);
        json.put("total", total);
        json.put("list",JSONArray.fromObject(obj));
        return json.toString();
    }
}
