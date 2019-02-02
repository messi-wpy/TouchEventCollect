package com.example.messi_lp.touchdemo;

import java.util.List;

public class NewData {


    /**
     * userID : 1
     * appName : qq
     * List : [[{"x":1024,"y":1920,"statu":0,"time":3894573847},{"x":1024,"y":1920,"statu":0,"time":3894573847},{"x":1024,"y":1920,"statu":0,"time":3894573847},{"x":1024,"y":1920,"statu":0,"time":3894573847}],[{"x":1024,"y":1920,"statu":0,"time":3894573847},{"x":1024,"y":1920,"statu":0,"time":3894573847},{"x":1024,"y":1920,"statu":0,"time":3894573847},{"x":1024,"y":1920,"statu":0,"time":3894573847}]]
     */

    private int userID;
    private String appName;
    private java.util.List<java.util.List<ListBean>> List;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<List<ListBean>> getList() {
        return List;
    }

    public void setList(List<List<ListBean>> List) {
        this.List = List;
    }

    public static class ListBean {
        /**
         * x : 1024
         * y : 1920
         * statu : 0
         * time : 3894573847
         */

        private int x;
        private int y;
        private int statu;
        private long time;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getStatu() {
            return statu;
        }

        public void setStatu(int statu) {
            this.statu = statu;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }
}
