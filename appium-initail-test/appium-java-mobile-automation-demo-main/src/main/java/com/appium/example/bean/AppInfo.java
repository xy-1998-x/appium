package com.appium.example.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppInfo {
    private String androidAppFilePath;
    private String androidAppPackage;
    private String androidAppActivity;
}
