package com.appium.example.constant;

public enum FindType {
    ID("id"),
    CLASSNAME("classname"),
    DESC("desc"),
    XPATH("xpath");

    private String name;

    FindType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
