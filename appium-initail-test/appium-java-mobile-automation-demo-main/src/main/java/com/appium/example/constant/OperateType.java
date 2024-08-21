package com.appium.example.constant;

public enum OperateType {
    CLICK("click"),
    INPUT("input"),
    SCROLLDOWN("scrolldown"),
    SCROLLRIGHT("scrollright"),
    SCROLLTOTEXT("scrolltotext"),
    FIND("find");


    private String name;

    OperateType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
