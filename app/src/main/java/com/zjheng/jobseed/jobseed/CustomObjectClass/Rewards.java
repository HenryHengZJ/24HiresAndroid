package com.zjheng.jobseed.jobseed.CustomObjectClass;

/**
 * Created by zhen on 2/18/2017.
 */

public class Rewards {

    private String title;
    private String points;
    private String image;

    public Rewards() {
    }

    public Rewards(String title, String points, String image) {
        this.title = title;
        this.points = points;
        this.image = image;
    }

    public String gettitle() {return title;}

    public void settitle(String title) {
        this.title = title;
    }

    public String getpoints() {return points;}

    public void setpoints(String points) {
        this.points = points;
    }

    public String getimage() {return image;}

    public void setimage(String image) {
        this.image = image;
    }

}

