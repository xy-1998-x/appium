package com.appium.example.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Task {
    private String appName;
    private String taskName;
    private List<Step> steps = new ArrayList<>();


    public void setSteps(Step step) {
        steps.add(step);
    }
}
