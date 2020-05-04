package com.zjheng.jobseed.jobseed.CustomObjectClass;

import static android.R.attr.rating;
import static com.zjheng.jobseed.jobseed.R.id.userimage;

/**
 * Created by zhen on 2/18/2017.
 */

public class TalentInfo {

    private String name;
    private String image;
    private String uid;
    private String desc;
    private String string_reviewstar;
    private String string_reviewcount;
    private String rates;
    private String postimage;
    private String title;
    private String dates;
    private String city;
    private String postkey;
    private String pressed;
    private String category;
    private String spinnercurrency;
    private String spinnerrate;
    private String basicpay;
    private String status;
    private String removed;
    private String location;
    private String newreview;
    private String reviewpressed;
    private String reviewed;
    private String verified;
    private Long newbookingcount;
    private Long totalbookingcount;
    private Long reviewstar;
    private Long reviewcount;

    public TalentInfo() {
    }

    public TalentInfo(String name, String image, String desc, Long reviewstar, Long reviewcount, String rates, String postimage, String string_reviewstar, String string_reviewcount,String uid,
                      String title, String dates, String city, String postkey, String pressed, String category, Long newbookingcount, Long totalbookingcount, String status, String removed,
                      String spinnercurrency, String spinnerrate, String basicpay, String location, String newreview, String reviewpressed, String reviewed, String verified) {
        this.name = name;
        this.image = image;
        this.desc = desc;
        this.reviewstar = reviewstar;
        this.reviewcount = reviewcount;
        this.rates = rates;
        this.postimage = postimage;
        this.title = title;
        this.dates = dates;
        this.city = city;
        this.postkey = postkey;
        this.pressed = pressed;
        this.category = category;
        this.newbookingcount = newbookingcount;
        this.totalbookingcount = totalbookingcount;
        this.string_reviewstar = string_reviewstar;
        this.string_reviewcount = string_reviewcount;
        this.status = status;
        this.removed = removed;
        this.uid = uid;
        this.spinnercurrency = spinnercurrency;
        this.spinnerrate = spinnerrate;
        this.basicpay = basicpay;
        this.location = location;
        this.newreview = newreview;
        this.location = location;
        this.newreview = newreview;
        this.reviewpressed = reviewpressed;
        this.reviewed = reviewed;
        this.verified = verified;
    }

    public String getverified() {return verified;}

    public void setverified(String verified) {
        this.verified = verified;
    }


    public String getreviewpressed() {return reviewpressed;}

    public void setreviewpressed(String reviewpressed) {
        this.reviewpressed = reviewpressed;
    }


    public String getreviewed() {return reviewed;}

    public void setreviewed(String reviewed) {
        this.reviewed = reviewed;
    }


    public String getnewreview() {return newreview;}

    public void setnewreview(String newreview) {
        this.newreview = newreview;
    }


    public String getlocation() {return location;}

    public void setlocation(String location) {
        this.location = location;
    }


    public String getspinnercurrency() {return spinnercurrency;}

    public void setspinnercurrency(String spinnercurrency) {
        this.spinnercurrency = spinnercurrency;
    }


    public String getspinnerrate() {return spinnerrate;}

    public void setspinnerrate(String spinnerrate) {
        this.spinnerrate = spinnerrate;
    }


    public String getbasicpay() {return basicpay;}

    public void setbasicpay(String basicpay) {
        this.basicpay = basicpay;
    }


    public String getuid() {return uid;}

    public void setuid(String uid) {
        this.uid = uid;
    }


    public String getremoved() {return removed;}

    public void setremoved(String removed) {
        this.removed = removed;
    }


    public String getstatus() {return status;}

    public void setstatus(String status) {
        this.status = status;
    }


    public String getpostkey() {return postkey;}

    public void setpostkey(String postkey) {
        this.postkey = postkey;
    }


    public String getpressed() {return pressed;}

    public void setpressed(String pressed) {
        this.pressed = pressed;
    }


    public String getcategory() {return category;}

    public void setcategory(String category) {
        this.category = category;
    }


    public String getcity() {return city;}

    public void setcity(String city) {
        this.city = city;
    }


    public String gettitle() {return title;}

    public void settitle(String title) {
        this.title = title;
    }


    public String getdates() {return dates;}

    public void setdates(String dates) {
        this.dates = dates;
    }


    public String getname() {return name;}

    public void setname(String name) {
        this.name = name;
    }


    public String getimage() {return image;}

    public void setimage(String image) {
        this.image = image;
    }


    public String getdesc() {return desc;}

    public void setdesc(String desc) {
        this.desc = desc;
    }


    public String getstring_reviewstar() {return string_reviewstar;}

    public void setstring_reviewstar(String string_reviewstar) {
        this.string_reviewstar = string_reviewstar;
    }



    public String getstring_reviewcount() {return string_reviewcount;}

    public void setstring_reviewcount(String string_reviewcount) {
        this.string_reviewcount = string_reviewcount;
    }


    public String getrates() {return rates;}

    public void setrates(String rates) {
        this.rates = rates;
    }


    public String getpostimage() {return postimage;}

    public void setpostimage(String postimage) {
        this.postimage = postimage;
    }


    public Long getnewbookingcount() {return newbookingcount;}

    public void setnewbookingcount(Long newbookingcount) {
        this.newbookingcount = newbookingcount;
    }


    public Long gettotalbookingcount() {return totalbookingcount;}

    public void settotalbookingcount(Long totalbookingcount) {
        this.totalbookingcount = totalbookingcount;
    }

    public Long getreviewstar() {return reviewstar;}

    public void setreviewstar(Long reviewstar) {
        this.reviewstar = reviewstar;
    }


    public Long getreviewcount() {return reviewcount;}

    public void setreviewcount(Long reviewcount) {
        this.reviewcount = reviewcount;
    }

}

