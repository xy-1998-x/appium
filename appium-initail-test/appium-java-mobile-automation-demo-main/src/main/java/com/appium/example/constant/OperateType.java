package com.appium.example.constant;

public enum OperateType {
    CLICK("click"),
    INPUT("input");

    private String name;

    OperateType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
