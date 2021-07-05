package com.xxxx.crm.query;

import com.xxxx.crm.base.BaseQuery;

public class SaleChanceQuery  extends BaseQuery {

    private String customerName;
    private String createMan;
    private String state;

    private Integer devResult; // 开发状态
    private Integer assignMan;// 分配人

    public SaleChanceQuery() {
    }

    public Integer getDevResult() {
        return devResult;
    }

    public void setDevResult(Integer devResult) {
        this.devResult = devResult;
    }

    public Integer getAssignMan() {
        return assignMan;
    }

    public void setAssignMan(Integer assignMan) {
        this.assignMan = assignMan;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreateMan() {
        return createMan;
    }

    public void setCreateMan(String createMan) {
        this.createMan = createMan;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
