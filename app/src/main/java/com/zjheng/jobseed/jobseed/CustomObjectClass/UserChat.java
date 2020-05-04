package com.zjheng.jobseed.jobseed.CustomObjectClass;

import static android.R.attr.id;
import static android.R.attr.rating;

/**
 * Created by zhen on 2/18/2017.
 */

public class UserChat {

    private String ownerid;
    private String receiverid;
    private String ownername;
    private String receivername;
    private String ownerimage;
    private String receiverimage;
    private String lastmessage;
    private String message;
    private Long time;
    private Long oldtime;
    private String newmessage;
    private String actiontitle;
    private String jobtitle;
    private String jobdescrip;
    private String city;
    private String postkey;
    private String maincategory;
    private String subcategory;

    public UserChat() {
    }

    public UserChat(String ownerid, String receiverid, String ownername, String receivername, String ownerimage, String receiverimage, String lastmessage,String maincategory,
                    String message, Long time, Long oldtime, String newmessage, String actiontitle, String jobtitle, String jobdescrip, String city, String postkey, String subcategory) {
        this.ownerid = ownerid;
        this.receiverid = receiverid;
        this.ownername = ownername;
        this.receivername = receivername;
        this.ownerimage = ownerimage;
        this.receiverimage = receiverimage;
        this.lastmessage = lastmessage;
        this.message = message;
        this.time = time;
        this.oldtime = oldtime;
        this.newmessage = newmessage;
        this.actiontitle = actiontitle;
        this.jobtitle = jobtitle;
        this.jobdescrip = jobdescrip;
        this.city = city;
        this.postkey = postkey;
        this.maincategory = maincategory;
        this.subcategory = subcategory;
    }

    public String getmaincategory() {return maincategory;}

    public void setmaincategory(String maincategory) {
        this.maincategory = maincategory;
    }


    public String getsubcategory() {return subcategory;}

    public void setsubcategory(String subcategory) {
        this.subcategory = subcategory;
    }


    public String getnewmessage() {return newmessage;}

    public void setnewmessage(String newmessage) {
        this.newmessage = newmessage;
    }


    public Long gettime() {return time;}

    public void settime(long time) {
        this.time = time;
    }


    public Long getoldtime() {return oldtime;}

    public void setoldtime(long oldtime) {
        this.oldtime = oldtime;
    }


    public String getownerid() {return ownerid;}

    public void setownerid(String ownerid) {
        this.ownerid = ownerid;
    }


    public String getreceiverid() {return receiverid;}

    public void setreceiverid(String receiverid) {
        this.receiverid = receiverid;
    }


    public String getownername() {
        return ownername;
    }

    public void setownername(String ownername) {
        this.ownername = ownername;
    }


    public String getreceivername() {
        return receivername;
    }

    public void setreceivername(String receivername) {
        this.receivername = receivername;
    }


    public String getownerimage() {
        return ownerimage;
    }

    public void setownerimage(String ownerimage) {
        this.ownerimage = ownerimage;
    }


    public String getreceiverimage() {
        return receiverimage;
    }

    public void setreceiverimage(String receiverimage) {
        this.receiverimage = receiverimage;
    }


    public String getmessage() {
        return message;
    }

    public void setmessage(String message) {
        this.message = message;
    }


    public String getlastmessage() {
        return lastmessage;
    }

    public void setlastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }


    public String getactiontitle() {
        return actiontitle;
    }

    public void setactiontitle(String actiontitle) {
        this.actiontitle = actiontitle;
    }


    public String getjobtitle() {
        return jobtitle;
    }

    public void setjobtitle(String jobtitle) {
        this.jobtitle = jobtitle;
    }


    public String getjobdescrip() {
        return jobdescrip;
    }

    public void setjobdescrip(String jobdescrip) {
        this.jobdescrip = jobdescrip;
    }


    public String getcity() {
        return city;
    }

    public void setcity(String city) {
        this.city = city;
    }


    public String getpostkey() {
        return postkey;
    }

    public void setpostkey(String postkey) {
        this.postkey = postkey;
    }

}

