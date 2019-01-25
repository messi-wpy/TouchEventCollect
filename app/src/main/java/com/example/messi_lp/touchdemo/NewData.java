package com.example.messi_lp.touchdemo;

import java.util.List;

public class NewData {

    /**
     * userID : 0
     * appName : string
     * List : [{"dataList":[{"x":0,"y":0,"statu":0,"time":"string"}]}]
     */

    private int userID;
    private String appName;
    private java.util.List<ListBean> List;

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

    public List<ListBean> getList() {
        return List;
    }

    public void setList(List<ListBean> List) {
        this.List = List;
    }

    public static class ListBean {
        private java.util.List<DataListBean> dataList;
        public ListBean(List<DataListBean> list){
            dataList=list;

        }
        public List<DataListBean> getDataList() {
            return dataList;
        }

        public void setDataList(List<DataListBean> dataList) {
            this.dataList = dataList;
        }

        public static class DataListBean {
            /**
             * x : 0
             * y : 0
             * statu : 0
             * time : string
             */

            private int x;
            private int y;
            private int statu;
            private String time;

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

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }
        }
    }
}
