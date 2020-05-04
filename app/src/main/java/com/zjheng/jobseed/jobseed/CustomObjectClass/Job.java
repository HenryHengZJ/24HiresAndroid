package com.zjheng.jobseed.jobseed.CustomObjectClass;

/**
 * Created by zhen on 2/12/2017.
 */

public class Job {

    private String title;
    private String lowertitle;
    private String desc;
    private String postimage;
    private String username;
    private String userimage;
    private String uid;
    private String city;
    private String fulladdress;
    private String postkey;
    private String category;
    private String company;
    private String status;
    private String pressed;
    private String closed;
    private String appliedkey;
    private String postedkey;
    private String wages;
    private String date;
    private String reviewpressed;
    private String reviewed;
    private Long time;
    private Long totalhiredcount;
    private Long applicantscount;
    private Long newapplicantscount;
    private int count;

    public Job(){
        
    }

    public Job(String title, String desc, String postimage, String username, String userimage, String uid, String city, String reviewed,
               String fulladdress, String postkey, String category, String company, String lowertitle, Long time, String closed,
               String status,  String pressed, Long totalhiredcount,Long applicantscount, String reviewpressed,
               Long newapplicantscount, String appliedkey, String postedkey, String wages, String date, int count) {
        this.title = title;
        this.desc = desc;
        this.postimage = postimage;
        this.username = username;
        this.userimage = userimage;
        this.uid = uid;
        this.city = city;
        this.fulladdress = fulladdress;
        this.postkey = postkey;
        this.category = category;
        this.company = company;
        this.lowertitle = lowertitle;
        this.time = time;
        this.closed = closed;
        this.status = status;
        this.pressed = pressed;
        this.appliedkey = appliedkey;
        this.postedkey = postedkey;
        this.count = count;
        this.totalhiredcount = totalhiredcount;
        this.applicantscount = applicantscount;
        this.newapplicantscount = newapplicantscount;
        this.wages = wages;
        this.date = date;
        this.reviewed = reviewed;
        this.reviewpressed = reviewpressed;
    }

    public String getreviewpressed() {return reviewpressed;}
    public void setreviewpressed(String reviewpressed) {this.reviewpressed = reviewpressed;}

    public int getcount() {return count;}
    public void setcount(int count) {this.count = count;}

    public String getreviewed() {return reviewed;}
    public void setreviewed(String reviewed) {this.reviewed = reviewed;}

    public String getWages() {return wages;}
    public void setWages(String wages) {this.wages = wages;}

    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}

    public String getpostImage() {return postimage;}
    public void setpostImage(String postimage) {this.postimage = postimage;}

    public String getpostedkey() {return postedkey;}
    public void setpostedkey(String postedkey) {this.postedkey = postedkey;}

    public String getappliedkey() {return appliedkey;}
    public void setappliedkey(String appliedkey) {this.appliedkey = appliedkey;}

    public String getstatus() {return status;}
    public void setstatus(String status) {this.status = status;}

    public String getpressed() {return pressed;}
    public void setpressed(String pressed) {this.pressed = pressed;}

    public Long gettime() {return time;}
    public void settime(long time) {this.time = time;}

    public Long gettotalhiredcount() {return totalhiredcount;}
    public void settotalhiredcount(long totalhiredcount) {this.totalhiredcount = totalhiredcount;}

    public Long getapplicantscount() {return applicantscount;}
    public void setapplicantscount(long applicantscount) {this.applicantscount = applicantscount;}

    public Long getnewapplicantscount() {return newapplicantscount;}
    public void setnewapplicantscount(long newapplicantscount) {this.newapplicantscount = newapplicantscount;}

    public String getclosed() {return closed;}
    public void setclosed(String closed) {
        this.closed = closed;
    }

    public String getTitle() {return title;}
    public void setTitle(String title) {
        this.title = title;
    }

    public String getLowertitle() {return lowertitle;}
    public void setLowertitle(String lowertitle) {
        this.lowertitle = lowertitle;
    }

    public String getDesc() {return desc;}
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getpostkey() {return postkey;}
    public void setpostkey(String postkey) {
        this.postkey = postkey;
    }

    public String getCategory() {return category;}
    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {return uid;}
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}

    public String getFulladdress() {return fulladdress;}
    public void setFulladdress(String fulladdress) {this.fulladdress = fulladdress;}

    public String getuserImage() {
        return userimage;
    }
    public void setuserImage(String userimage) {this.userimage = userimage;}

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {this.company = company;}

}
