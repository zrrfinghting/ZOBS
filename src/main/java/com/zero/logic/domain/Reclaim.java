package com.zero.logic.domain;

import com.zero.basic.domain.BasicBean;

import javax.persistence.*;
import java.util.List;

/**
 * 在线回收
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/8/2
 */
@Entity
@Table(name = "sys_reclaim")
public class Reclaim extends BasicBean{

    @Id
    @Column(name = "RECLAIMID")
    private String reclaimId;//

    @Column(name = "SELLERNAME")
    private String sellerName;//卖家姓名

    @Column(name = "SELLERPHONE")
    private String sellerPhone;//卖家电话

    @Column(name = "SELLERADDR")//卖家地址
    private String sellerAddr;

    @Column(name = "GOODSDSC")
    private String goodsDsc;//货物描述

    @Column(name = "IMGS")
    @ElementCollection
    private List<String> imgs;//货物图片

    @Column(name = "NUMBER")
    private int number;//获取数量 单位：本

    @Column(name = "STATE")
    private int state;//0等待商家查看，1达成交易，2完成交易,3拒绝回收

    @Column(name = "TRADPRICE")
    private double tradPrice;//交易价格

    @Column(name = "NOTES")
    private String notes;//备注

    public String getReclaimId() {
        return reclaimId;
    }

    public void setReclaimId(String reclaimId) {
        this.reclaimId = reclaimId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getSellerAddr() {
        return sellerAddr;
    }

    public void setSellerAddr(String sellerAddr) {
        this.sellerAddr = sellerAddr;
    }

    public String getGoodsDsc() {
        return goodsDsc;
    }

    public void setGoodsDsc(String goodsDsc) {
        this.goodsDsc = goodsDsc;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public double getTradPrice() {
        return tradPrice;
    }

    public void setTradPrice(double tradPrice) {
        this.tradPrice = tradPrice;
    }

    public String getNotes() {return notes;}

    public void setNotes(String notes) {this.notes = notes;}
}
