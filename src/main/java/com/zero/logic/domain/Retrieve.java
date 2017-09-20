package com.zero.logic.domain;

import com.zero.basic.domain.BasicBean;
import com.zero.logic.util.DateUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.text.ParseException;
import java.util.Date;

/**
 * 回收单类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/27
 */
@Entity
@Table(name = "sys_retrieve")
public class Retrieve extends BasicBean{
    //回收单编号
    @Id
    @Column(name = "RETRIEVEID")
    private String retrieveId;
    //回收人
    @Column(name = "RETRIEVER")
    private String retriever;
    //回收人电话
    @Column(name ="RETRIEVEPHONE")
    private String retrieverPhone;
    //回收人地址
    @Column(name = "RETRIEVERADDRESS")
    private String retrieverAddress;
    //回收日期
    @Column(name = "RETRIEVEDATE")
    private Date retrieveDate;
    //回收状态：主动回收1，退货回收0
    @Column(name = "STATE")
    private int state;
    //如果是退货回收，则会有原来的订单编号
    @Column(name = "ORDERID")
    private String orderId;

    public String getRetrieveId() {
        return retrieveId;
    }

    public void setRetrieveId(String retrieveId) {
        this.retrieveId = retrieveId;
    }

    public String getRetriever() {
        return retriever;
    }

    public void setRetriever(String retriever) {
        this.retriever = retriever;
    }

    public String getRetrieverPhone() {
        return retrieverPhone;
    }

    public void setRetrieverPhone(String retrieverPhone) {
        this.retrieverPhone = retrieverPhone;
    }

    public String getRetrieverAddress() {
        return retrieverAddress;
    }

    public void setRetrieverAddress(String retrieverAddress) {
        this.retrieverAddress = retrieverAddress;
    }

    public String getRetrieveDate() throws ParseException {
        return DateUtil.formatDate(DateUtil.FORMAT2,retrieveDate);
    }

    public void setRetrieveDate(Date retrieveDate) {
        this.retrieveDate = retrieveDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
