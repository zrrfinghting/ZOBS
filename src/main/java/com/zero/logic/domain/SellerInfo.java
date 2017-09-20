package com.zero.logic.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商家信息
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/31
 */
@Entity
@Table(name = "sys_seller_info")
public class SellerInfo {
    @Id
    @Column(name = "SELLERCODE")
    private String sellerCode;//商家编号
    @Column(name = "SELLERNAME")
    private String sellerName;//商家名称
    @Column(name = "SELLERADDR")
    private String sellerAddr;//商家地址
    @Column(name = "PHONE")
    private String phone;//商家联系电话
    @Column(name = "EMAIL")
    private String email;//商家邮件
    @Column(name = "ALIPAY")
    private String alipay;//商家账号

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerAddr() {
        return sellerAddr;
    }

    public void setSellerAddr(String sellerAddr) {
        this.sellerAddr = sellerAddr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }
}
