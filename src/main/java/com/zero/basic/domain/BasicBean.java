package com.zero.basic.domain;

import com.zero.logic.util.DateUtil;
import javax.persistence.*;
import java.text.ParseException;
import java.util.Date;

/**
 * 基础实体类
 * @auther Deram Zhao
 * @creatTime 2017/6/1
 */
@MappedSuperclass
public class BasicBean {

    @Column(name = "CREATEUSER")
    private String createUser;
    @Column(name = "CREATEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Column(name = "UPDATEDATE")
    private Date updateDate;
    @Column(name = "UPDATEUSER")
    private String updateUser;

    //getter和setter方法
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateDate() throws ParseException {
        return DateUtil.formatDate(DateUtil.FORMAT2,createDate);
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() throws ParseException {
        return DateUtil.formatDate(DateUtil.FORMAT2,updateDate);
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}
