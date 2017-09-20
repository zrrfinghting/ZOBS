package com.zero.logic.domain;

/**
 * 热销树的结果集对象类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/10
 */
public class TopSell {
    private String bookId;
    private String bookNum;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookNum() {
        return bookNum;
    }

    public void setBookNum(String bookNum) {
        this.bookNum = bookNum;
    }
}
