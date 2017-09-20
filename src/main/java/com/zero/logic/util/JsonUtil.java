package com.zero.logic.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * json格式
 * @autherAdmin Deram Zhao
 * @creat 2017/6/1
 */
public class JsonUtil {
    public static final int RESULT_FAIL = 500;    //操作失败
    public static final int RESULT_SUCCESS = 200; //操作成功

    /**
     * JSON格式返回值字符串
     * @param status 返回状态
     * @param msg 返回信息
     * @return  字符串
     */
    public static String returnStr(int status,String msg) {
        try {
            JSONObject json = new JSONObject();
            json.put("status",status);
            json.put("msg",msg);
            return json.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将对象转换成JSONObject
     * @param obj 对象
     * @return jsonObject类型的字符串
     * @throws Exception
     */
    public static String fromObject(Object obj)throws Exception{
        JSONObject jsonObject = JSONObject.fromObject(obj);
        return jsonObject.toString();
    }

    /**
     * 将对象转换成JSON,添加新的键值
     * @param obj  对象
     * @param map  键值集合
     * @return JSONObject类型的字符串
     * @throws Exception
     */
    public static String makeJsonBeanByKey(Object obj,Map<String, Object> map)throws Exception{
        JSONObject jsonObject=JSONObject.fromObject(obj);
        Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toString();
    }

    /**
     * 将对象转换成JSONArray
     * @param obj 对象
     * @return JSONArray类型的字符串
     * @throws Exception
     */
    public static String fromArray(Object obj)throws Exception{
        JSONArray jSONArray = JSONArray.fromObject(obj);
        return jSONArray.toString();
    }

    /**
     * 获取JSON对象中的值
     * @param key 字段
     * @param obj 对象
     * @return 字符串值
     * @throws Exception
     */
    public static String getString(String key,Object obj)throws Exception{
        JSONObject jsonObject=JSONObject.fromObject(obj);
        if(jsonObject.containsKey(key)){
            return jsonObject.getString(key);
        }else{
            return null;
        }
    }

    /**
     * 将JSONArray 转换成 list
     * @param object
     * @return list集合
     */
    public static List getList(Object object){
        JSONArray jsonArray = JSONArray.fromObject(object);
        List<Object> list = new ArrayList<>();
        for (Object obj:jsonArray){
            list.add(obj);
        }
        return  list;
    }
}
