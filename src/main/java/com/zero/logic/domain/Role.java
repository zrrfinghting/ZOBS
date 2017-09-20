package com.zero.logic.domain;

import com.zero.basic.domain.BasicBean;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体类
 * @auther Deram Zhao
 * @creatTime 2017/6/8
 */

@Entity
@Table(name = "sys_role")
public class Role extends BasicBean {
    @Id
    @Column(name = "ROLEID")
    //角色编号
    private String roleId;
    @Column(name = "ROLENAME")
    //角色名称
    private String roleName;
    @Column(name = "ROLEDESC")
    //备注
    private String roleDesc;
    @Column(name = "STATE")
    //0为停用、1为启用
    private int state;
    @ManyToMany
    @JoinTable(name="sys_role_purview",
               inverseJoinColumns=@JoinColumn(name="purviewId",referencedColumnName="purviewId"),
               joinColumns=@JoinColumn(name="roleId",referencedColumnName="roleId"))
    private Set<Purview> purviews=new HashSet<>();

    //getter和setter方法
    public String getRoleId() {return roleId;}

    public void setRoleId(String roleId) {this.roleId = roleId;}

    public String getRoleName() {return roleName;}

    public void setRoleName(String roleName) {this.roleName = roleName;}

    public String getRoleDesc() {return roleDesc;}

    public void setRoleDesc(String roleDesc) {this.roleDesc = roleDesc;}

    public int getState() {return state;}

    public void setState(int state) {this.state = state;}

    public Set<Purview> getPurviews() {
        return purviews;
    }

    public void setPurviews(Set<Purview> set) {
        this.purviews = purviews;
    }
}
