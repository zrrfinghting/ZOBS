package com.zero.logic.util;

import com.zero.logic.dao.BookTypeDao;
import com.zero.logic.domain.BookType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * 编码生成器工具类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/28
 */
public class CodeGeneratorUtil {

    public static String getRetrieveCode(){
        return String.valueOf(System.currentTimeMillis());
    }
    /**
     * 生成购物车编号
     * @return 购物车编号
     */
    public static String getShopCartCode(){
        return String.valueOf(System.currentTimeMillis());
    }
    /**
     * 订单编号
     * @return 订单编号
     */
    public static String getOrderCode(){
        return String.valueOf(System.currentTimeMillis());
    }
    /**
     *生成书籍编号
     * @return  书籍编号
     */
    public static String getBookCode(){
        long date = System.currentTimeMillis();
        String bookCode = String.valueOf(date);
        return bookCode;
    }

    /**
     * 生成书籍分类编号
     * @param parent
     * @param iterable
     * @return 分类编号
     */
    public static String getBookTypeCode(String parent,Iterable<BookType> iterable){
        String result = "";
        int max = 0;
        char ch [] = new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        if ("".equals(parent) || null==parent){
            for (int i=0;i<ch.length;i++){
                Boolean flag = false;
                for (BookType bookType:iterable){
                    if (bookType.getTypeId().equals(String.valueOf(ch[i]))){
                        flag = true;break;
                    }
                }
                if (!flag){
                    result = String.valueOf(ch[i]);
                    break;
                }
            }
            return result;
        }else {
            for (BookType bookType:iterable){
                String typeId = bookType.getTypeId();
                if (typeId.startsWith(parent.substring(0,1))){
                    if (typeId.length()==parent.length()+2){
                        int i = Integer.parseInt(typeId.substring(typeId.length()-2,typeId.length()));
                       max=(i>max)?i:max;
                    }
                }
            }
            if (max+1>=10){
                max +=1;
                result = parent+max;
            }else if (max+1<10){
                max +=1;
                result = parent+"0"+max;
            }
        }
        return result;
    }
}
