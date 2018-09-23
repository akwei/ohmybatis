package com.github.akwei.ohmybatis.example.entity;

import com.github.akwei.ohmybatis.annotations.NotColumn;

import javax.persistence.Id;
import javax.persistence.Table;

//use jpa interface annotation
//name = table name
@Table(name = "user")
public class User {

    //mark userid is a primary key of table user.
    //table can have multi @Id property, like composite-id
    @Id
    private long userid;

    private String nick;

    private Integer sex;

    private String addr;

    private boolean enableflag;

    private int level;

    private long createtime;

    //use @NotColumn mark the property is not a table column
    @NotColumn
    private String otherField;

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public boolean isEnableflag() {
        return enableflag;
    }

    public void setEnableflag(boolean enableflag) {
        this.enableflag = enableflag;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getOtherField() {
        return otherField;
    }

    public void setOtherField(String otherField) {
        this.otherField = otherField;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }
}
