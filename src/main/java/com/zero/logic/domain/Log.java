package com.zero.logic.domain;
import com.zero.basic.domain.BasicBean;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 日志类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/9
 */
@Entity
@Table(name = "sys_log")
public class Log extends BasicBean {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "LOGID")
    private String logId;//日志id 
    @Column(name = "LOGCONTENT")
    private String logContent;//日志内容
    @Column(name = "TYPE")
    private int type;//日志类型 0=操作日志；1=数据库日志；2=系统日志
    @Column(name = "LEVER")
    private int lever;//日志级别 0=debug；1=info；2=warn；3=errer
    @Column(name = "USERCODE")
    private String userCode;//操作用户

    public Log(){}

    public Log(Date creatDate,Date updateDate, String logContent, int type, String userCode){
        this.setCreateDate(creatDate);
        this.setLogContent(logContent);
        this.setType(type);
        this.setUserCode(userCode);

    }
    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLever() {
        return lever;
    }

    public void setLever(int lever) {
        this.lever = lever;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
