package com.zjheng.jobseed.jobseed.CustomObjectClass;

import static android.R.attr.category;
import static com.zjheng.jobseed.jobseed.R.drawable.wages;
import static com.zjheng.jobseed.jobseed.R.id.userimage;

/**
 * Created by zhen on 2/12/2017.
 */

public class AppliedClass {

    private String title;
    private String desc;
    private String postimage;
    private String uid;
    private String city;
    private String postkey;
    private String company;
    private String pressed;
    private String closed;
    private String status;
    private Long time;

    public AppliedClass(){

    }

    public AppliedClass(String title, String desc, String postimage, String uid, String city,
                        String postkey, String company, Long time, String closed,
                        String status, String pressed) {
        this.title = title;
        this.desc = desc;
        this.postimage = postimage;
        this.uid = uid;
        this.city = city;
        this.postkey = postkey;
        this.company = company;
        this.time = time;
        this.closed = closed;
        this.status = status;
        this.pressed = pressed;
    }

    public String getpostImage() {return postimage;}
    public void setpostImage(String postimage) {this.postimage = postimage;}

    public String getstatus() {return status;}
    public void setstatus(String status) {this.status = status;}

    public String getpressed() {return pressed;}
    public void setpressed(String pressed) {this.pressed = pressed;}

    public Long gettime() {return time;}
    public void settime(long time) {this.time = time;}

    public String getclosed() {return closed;}
    public void setclosed(String closed) {
        this.closed = closed;
    }

    public String getTitle() {return title;}
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {return desc;}
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getpostkey() {return postkey;}
    public void setpostkey(String postkey) {
        this.postkey = postkey;
    }

    public String getUid() {return uid;}
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {this.company = company;}

}
