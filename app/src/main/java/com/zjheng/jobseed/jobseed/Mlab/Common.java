package com.zjheng.jobseed.jobseed.Mlab;

/**
 * Created by zhen on 6/4/2018.
 */

public class Common {

    private static String DB_NAME = "henrytest1";
    private static String COLLECTION_NAME = "jobtest1";
    public static String API_KEY = "seQ5SmvzsIPWJcvB_Pd4_MB1gpXY29kL";

    public static String getAddressApi() {
        String baseurl = String.format("https://api.mlab.com/api/1/databases/%s/collections/%s", DB_NAME, COLLECTION_NAME );
        StringBuilder stringBuilder = new StringBuilder(baseurl);
        stringBuilder.append("?apiKey="+API_KEY);
        return stringBuilder.toString();
    }
}
