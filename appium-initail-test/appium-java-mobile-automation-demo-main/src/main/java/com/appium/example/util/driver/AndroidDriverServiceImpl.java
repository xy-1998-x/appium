package com.appium.example.util.driver;

import com.appium.example.bean.AppInfo;
import com.appium.example.constant.DriverConstants;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;

import java.io.File;

public class AndroidDriverServiceImpl implements MobileDriverService {
    private AndroidDriver androidDriver;
    private AppInfo appInfo;

    @Override
    public void initAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    @Override
    public void spinUpDriver(AppiumDriverLocalService appiumService) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setUdid(DriverConstants.ANDROID_DEVICE_NAME)   //不设置就是默认
                .setAppPackage(appInfo.getAndroidAppPackage())
                .setAppActivity(appInfo.getAndroidAppActivity())

                .setNoReset(Boolean.parseBoolean(DriverConstants.ANDROID_NO_RESET))
                .setFullReset(Boolean.parseBoolean(DriverConstants.ANDROID_FULL_RESET))
                .autoGrantPermissions();

        androidDriver = new AndroidDriver(appiumService.getUrl(), options); //.
        androidDriver.manage().timeouts().implicitlyWait(DriverConstants.APPIUM_DRIVER_TIMEOUT);
    }

    @Override
    public void closeDriver() {
        androidDriver.terminateApp(appInfo.getAndroidAppPackage());
    }

    @Override
    public AppiumDriver getDriver() {
        return androidDriver;
    }
}
