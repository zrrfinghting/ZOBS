package com.zero.logic.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 密码输入错误次数
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/20
 */
public class PswErrorNumUtil {
    public static Map<String,Object> map = new HashMap<>();
    public static void setErrorNum(String userCode){
        int erroeNum = 0;
        if (null!=map.get(userCode)){
            erroeNum = Integer.parseInt(map.get(userCode).toString());
        }
        map.put(userCode,erroeNum+1);
    }
}
