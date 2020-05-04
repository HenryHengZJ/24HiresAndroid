package com.zjheng.jobseed.jobseed.CustomObjectClass;

/**
 * Created by zhen on 2/18/2017.
 */

public class UserReview {

    private String username;
    private String userimage;
    private String reviewmessage;
    private String useruid;
    private Long time;
    private int rating;

    public UserReview() {
    }

    public UserReview(String username, String userimage, String reviewmessage, Long time, int rating, String useruid) {
        this.username = username;
        this.userimage = userimage;
        this.reviewmessage = reviewmessage;
        this.useruid = useruid;
        this.time = time;
        this.rating = rating;
    }

    public String getusername() {return username;}

    public void setusername(String username) {
        this.username = username;
    }

    public String getuseruid() {return useruid;}

    public void setuseruid(String useruid) {
        this.useruid = useruid;
    }

    public int getrating() {return rating;}

    public void setrating(int rating) {
        this.rating = rating;
    }


    public Long gettime() {return time;}

    public void settime(long time) {
        this.time = time;
    }


    public String getuserimage() {return userimage;}

    public void setuserimage(String userimage) {
        this.userimage = userimage;
    }


    public String getreviewmessage() {return reviewmessage;}

    public void setreviewmessage(String reviewmessage) {
        this.reviewmessage = reviewmessage;
    }
}

