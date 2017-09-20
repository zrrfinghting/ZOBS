package com.zero.logic.domain;/**
 * Created by Admin on 2017/7/4.
 */

import java.io.Serializable;

/**
 * 回收单书籍复合主键
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/4
 */
public class RetrieveBookPK implements Serializable {
    //回收单编号
    private String retrieveId;
    //书籍编号
    private String bookId;

    public String getRetrieveId() {
        return retrieveId;
    }

    public void setRetrieveId(String retrieveId) {
        this.retrieveId = retrieveId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
