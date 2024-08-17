package com.appium.example.bean;

import com.appium.example.constant.FindType;
import com.appium.example.constant.OperateType;
import lombok.Data;

@Data
public class Step {
    private String appName;
    private String activityName;
    private String elementInfo;
    private FindType findType;
    private OperateType operateType;
    private String inputText;
    private int index;
}
