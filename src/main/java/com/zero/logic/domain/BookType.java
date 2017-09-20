package com.zero.logic.domain;/**
 * Created by Admin on 2017/6/15.
 */

import com.zero.basic.domain.BasicBean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 图书分类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/15
 */
@Entity
@Table(name = "sys_book_type")
public class BookType extends BasicBean{

    //分类ID
    @Id
    @Column(name = "TYPEID")
    public String typeId;
    //分类名称
    @Column(name = "TYPENAME")
    public String typeName;
    //父分类
    @Column(name = "PARENT")
    public String parent;
    //分类描述
    @Column(name = "TYPEDESC")
    public String typeDesc;
    //排序
    @Column(name = "ORDERBY")
    public int ordeBy;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public int getOrdeBy() {
        return ordeBy;
    }

    public void setOrdeBy(int ordeBy) {
        this.ordeBy = ordeBy;
    }
}
