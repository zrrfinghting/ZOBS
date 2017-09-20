package com.zero.logic.domain;

import com.fasterxml.jackson.databind.node.DoubleNode;

import javax.persistence.*;

/**
 * 回收单图书类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/27
 */

@Entity
@IdClass(RetrieveBookPK.class)
@Table(name = "sys_retrieve_book")
public class RetrieveBook {

    //回收单编号
    @Id
    @Column(name = "RETRIEVEID")
    private String retrieveId;
    //图书ID
    @Id
    @Column(name = "BOOKID")
    private String bookId;
    //回收数量
    @Column(name = "BOOKNUM")
    private int bookNum;
    //回收价格
    @Column(name = "PRICE")
    private double price;

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

    public int getBookNum() {
        return bookNum;
    }

    public void setBookNum(int bookNum) {
        this.bookNum = bookNum;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
