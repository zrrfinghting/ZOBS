package com.zero.logic.util;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式工具类
 * @auther Deram Zhao
 * @creatTime 2017/6/7
 */
public class DateUtil {
    //格式常量
    public static final DateFormat FORMAT1 = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat FORMAT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat FORMAT3 = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final DateFormat FORMAT4 = new SimpleDateFormat("yyyy-MM");
    public static final DateFormat FORMAT5 = new SimpleDateFormat("HH:mm:ss");


    /**
     *  将字符串格式的日期转成日期类型
     * @param format 格式
     * @param dateStr 字符串日期
     * @return 日期类型
     * @throws ParseException
     */
    public static Date parse(DateFormat format, String dateStr) throws ParseException {
        if (null==dateStr || "".equals(dateStr)){
            return null;
        }
        return format.parse(dateStr);
    }

    /**
     *  将日期类型转成字符串格式的日期
     * @param format 格式
     * @param date 日期
     * @return 字符串日期类型
     * @throws ParseException
     */
    public static String formatDate(DateFormat format,Date date)throws ParseException{
        if (null!=date){
            return format.format(date);
        }else {
            return null;
        }
    }

}
