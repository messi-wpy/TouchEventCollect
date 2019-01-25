package com.example.messi_lp.touchdemo;

import java.util.List;

public class ResultData {


    private List<OperationIDListBean> operationIDList;

    public List<OperationIDListBean> getOperationIDList() {
        return operationIDList;
    }

    public void setOperationIDList(List<OperationIDListBean> operationIDList) {
        this.operationIDList = operationIDList;
    }

    public static class OperationIDListBean {
        /**
         * ID : 0
         */

        private int ID;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }
    }
}
