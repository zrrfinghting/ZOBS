package com.zero.logic.domain;

import com.zero.basic.domain.BasicBean;

import javax.persistence.*;

/**
 *
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/7
 */
@Entity
@IdClass(ShopCartPK.class)
@Table(name = "sys_shop_cart")
public class ShopCart extends BasicBean{
    //
    @Id
    @Column(name = "USERCODE")
    private String userCode;

    @Id
    @Column(name = "BOOKID")
    private String bookId;

    @Column(name = "BOOKNUM")
    private int bookNum;

    @Column(name = "IMAGE_L")
    private String image_l;

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

    public int getBookNum() {
        return bookNum;
    }

    public void setBookNum(int bookNum) {
        this.bookNum = bookNum;
    }

    public String getImage_l() {
        return image_l;
    }

    public void setImage_l(String image_l) {
        this.image_l = image_l;
    }
}
