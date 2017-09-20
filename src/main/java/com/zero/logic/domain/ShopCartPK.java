package com.zero.logic.domain;

import java.io.Serializable;

/**
 * 购物车复合主键(用户编号和货物编号来确定购物车里的货物与用户关系)
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/7
 */
public class ShopCartPK implements Serializable {
    //用户编号
    private String userCode;
    //书籍编号
    private String bookId;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
