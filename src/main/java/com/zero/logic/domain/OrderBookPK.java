package com.zero.logic.domain;

import java.io.Serializable;

/**
 * 订单图书表复合主键
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/19
 */
public class OrderBookPK implements Serializable{
    //订单编号
    private String orderId;
    //图书编号
    private String bookId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
