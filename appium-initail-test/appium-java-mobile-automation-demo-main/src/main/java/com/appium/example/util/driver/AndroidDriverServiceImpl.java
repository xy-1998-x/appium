package com.appium.example.util.driver;

import com.appium.example.bean.AppInfo;
import com.appium.example.constant.DriverConstants;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;

import java.io.File;

//具体这个接口的实现过程 用AndroidDriverServiceImpl这个类实现这个接口
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

        //将这个功能设置为true意味着在测试过程中，如果应用已经在运行，Appium 会先关闭它然后重新启动应用。
        //确保应用以一个已知的状态开始测试，避免之前的测试运行对当前测试产生影
        options.setCapability("appium:forceAppLaunch", true);

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
