package com.zero.logic.domain;/**
 * Created by Admin on 2017/6/12.
 */

import com.zero.basic.domain.BasicBean;
import com.zero.logic.util.DateUtil;

import javax.persistence.*;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 图书类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/12
 */
@Entity
@Table(name = "sys_book")
public class Book extends BasicBean{

    @Id
    @Column(name = "BOOKID")
    //图书编号
    private String bookId;
    @Column(name = "BOOKNAME")
    //图书名称
    private String bookName;//
    @Column(name = "AUTHOR")
    //作者
    private String author;//
    @Column(name = "PRICE")
    //价格
    private double price;//
    @Column(name = "DISCOUNT")
    //折扣
    private double discount;

    public int getBookNum() {
        return bookNum;
    }

    public void setBookNum(int bookNum) {
        this.bookNum = bookNum;
    }

    @Column(name = "PRESS")
    //出版社
    private String press;
    @Column(name = "PUBLISHTIME")
    //出版时间
    private Date publishTime;
    @Column(name = "EDITION")
    //版次
    private int edition;
    @Column(name = "PAGENUM")
    //页数
    private int pageNum;
    @Column(name = "WORDNUM")
    //字数
    private int WordNum;
    @Column(name = "PRINTTIME")
    //印刷时间
    private Date printtime;
    @Column(name = "BOOKSIZE")
    //开本
    private int bookSize;
    @Column(name = "PAGER")
    //纸质
    private String paper;
    @Column(name = "TYPEID")
    //所属分类
    private String typeId;
    @Column(name = "IMAGE_L")
    //大图路径
    private String image_l;
    @Column(name = "IMAGE_S")
    //小图路径
    private String  image_s;
    @Column(name = "ORDERBY")
    //排序
    private int orderBy;
    @Column(name = "STOREHOUSE")
    //仓库位置
    private String storehouse;
    @Column(name = "BOOKDESC")
    //图书简介
    private String bookDesc;
    @Column(name = "STATE")
    //图书状态
    private int state;//未上架0，已经上架1；
    @Column(name = "BOOKNUM")
    //库存量
    private int bookNum;


    //getter和setter方法
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getPublishTime() throws ParseException {
        return DateUtil.formatDate(DateUtil.FORMAT2,publishTime);
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getWordNum() {
        return WordNum;
    }

    public void setWordNum(int wordNum) {
        WordNum = wordNum;
    }

    public String getPrinttime() throws ParseException {
        return DateUtil.formatDate(DateUtil.FORMAT2,printtime);
    }

    public void setPrinttime(Date printtime) {
        this.printtime = printtime;
    }

    public int getBookSize() {
        return bookSize;
    }

    public void setBookSize(int bookSize) {
        this.bookSize = bookSize;
    }

    public String getPaper() {
        return paper;
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getImage_l() {
        return image_l;
    }

    public void setImage_l(String image_l) {
        this.image_l = image_l;
    }

    public String getImage_s() {
        return image_s;
    }

    public void setImage_s(String image_s) {
        this.image_s = image_s;
    }

    public int getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(int orderBy) {
        this.orderBy = orderBy;
    }

    public String getStorehouse() {
        return storehouse;
    }

    public void setStorehouse(String storehouse) {
        this.storehouse = storehouse;
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


}
