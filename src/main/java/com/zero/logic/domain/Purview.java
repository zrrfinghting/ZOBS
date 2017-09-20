package com.zero.logic.domain;

import com.zero.basic.domain.BasicBean;

import javax.persistence.*;

/**
 * 权限实体类
 * @auther Deram Zhao
 * @creatTime 2017/6/9
 */
@Entity
@Table(name = "sys_purview")
public class Purview extends BasicBean {
    @Id
    @Column(name = "PURVIEWID")
    //权限编号
    private String purviewId;
    @Column(name = "PURVIEWNAME")
    //权限名称
    private String purviewName;
    @Column(name = "PURVIEWRULE")
    //权限规则
    private String purviewRule;
    @Column(name = "PURVIEWDESC")
    //权限备注
    private String purviewDesc;
    @Column(name = "STATE")
    //权限状态 0--停用，1--启用
    private int state;
    //getter和setter方法
    public String getPurviewId() {
        return purviewId;
    }

    public void setPurviewId(String purviewId) {
        this.purviewId = purviewId;
    }

    public String getPurviewName() {
        return purviewName;
    }

    public void setPurviewName(String purviewName) {
        this.purviewName = purviewName;
    }

    public String getPurviewRule() {
        return purviewRule;
    }

    public void setPurviewRule(String purviewRule) {
        this.purviewRule = purviewRule;
    }

    public String getPurviewDesc() {
        return purviewDesc;
    }

    public void setPurviewDesc(String purviewDesc) {
        this.purviewDesc = purviewDesc;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
