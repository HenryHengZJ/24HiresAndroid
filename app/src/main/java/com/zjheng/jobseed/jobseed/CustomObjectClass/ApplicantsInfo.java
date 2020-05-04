package com.zjheng.jobseed.jobseed.CustomObjectClass;

/**
 * Created by zhen on 2/18/2017.
 */

public class ApplicantsInfo {

    private String name;
    private String location;
    private String image;
    private String date;
    private String userid;
    private String worktitle1;
    private String workcompany1;
    private String worktitle2;
    private String workcompany2;
    private String worktitle3;
    private String workcompany3;
    private String pressed;
    private String reviewpressed;
    private String reviewed;
    private String offerstatus;
    private Long time;
    private Long newapplicantscount;

    public ApplicantsInfo() {
    }

    public ApplicantsInfo(String name, String location, String worktitle1, String workcompany1, String worktitle2, String workcompany2, String reviewpressed, String reviewed,
                          String worktitle3, String workcompany3, String image, String userid, Long time, Long newapplicantscount, String pressed, String date, String offerstatus) {
        this.name = name;
        this.location = location;
        this.worktitle1 = worktitle1;
        this.workcompany1 = workcompany1;
        this.worktitle2 = worktitle2;
        this.workcompany2 = workcompany2;
        this.worktitle3 = worktitle3;
        this.workcompany3 = workcompany3;
        this.image = image;
        this.userid = userid;
        this.time = time;
        this.pressed = pressed;
        this.newapplicantscount = newapplicantscount;
        this.reviewpressed = reviewpressed;
        this.reviewed = reviewed;
        this.date = date;
        this.offerstatus =offerstatus;
    }

    public String getofferstatus() {return offerstatus;}
    public void setofferstatus(String offerstatus) {this.offerstatus = offerstatus;}

    public String getdate() {return date;}
    public void setdate(String date) {this.date = date;}

    public String getreviewpressed() {return reviewpressed;}
    public void setreviewpressed(String reviewpressed) {this.reviewpressed = reviewpressed;}

    public String getreviewed() {return reviewed;}
    public void setreviewed(String reviewed) {this.reviewed = reviewed;}

    public String getpressed() {return pressed;}
    public void setpressed(String pressed) {this.pressed = pressed;}

    public Long gettime() {return time;}
    public void settime(long time) {this.time = time;}

    public Long getnewapplicantscount() {return newapplicantscount;}
    public void setnewapplicantscount(long newapplicantscount) {this.newapplicantscount = newapplicantscount;}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getuserid() {
        return userid;
    }
    public void setuserid(String userid) {this.userid = userid;}

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getlocation() {
        return location;
    }
    public void setlocation(String location) {this.location = location;}

    public String getworktitle1() {
        return worktitle1;
    }
    public void setworktitle1(String worktitle1) {
        this.worktitle1 = worktitle1;
    }

    public String getworkcompany1() {
        return workcompany1;
    }
    public void setworkcompany1(String workcompany1) {
        this.workcompany1 = workcompany1;
    }

    public String getworktitle2() {
        return worktitle2;
    }
    public void setworktitle2(String worktitle2) {
        this.worktitle2 = worktitle2;
    }

    public String getworkcompany2() {
        return workcompany2;
    }
    public void setworkcompany2(String workcompany2) {
        this.workcompany2 = workcompany2;
    }

    public String getworktitle3() {
        return worktitle3;
    }
    public void setworktitle3(String worktitle3) {
        this.worktitle3 = worktitle3;
    }

    public String getworkcompany3() {
        return workcompany3;
    }
    public void setworkcompany3(String workcompany3) {
        this.workcompany3 = workcompany3;
    }
}

