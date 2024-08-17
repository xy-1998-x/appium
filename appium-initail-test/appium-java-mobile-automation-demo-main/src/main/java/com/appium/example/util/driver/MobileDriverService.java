package com.appium.example.util.driver;

import com.appium.example.bean.AppInfo;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

import static com.appium.example.constant.DriverConstants.APPIUM_SERVER_IP;
import static com.appium.example.constant.DriverConstants.APPIUM_SERVER_PORT;

public interface MobileDriverService {
    default AppiumDriverLocalService startAppiumService() {
        AppiumDriverLocalService appiumService = AppiumDriverLocalService.buildService(
                new AppiumServiceBuilder()
                        .withIPAddress(APPIUM_SERVER_IP)
                        .usingPort(APPIUM_SERVER_PORT)
        );
        if (appiumService.isRunning()) {
            appiumService.stop();
        }
        appiumService.start();
        return appiumService;
    }

    default void stopAppiumService(AppiumDriverLocalService appiumService) {
        appiumService.stop();
    }

    void initAppInfo(AppInfo appInfo);

    void spinUpDriver(AppiumDriverLocalService appiumService);

    void closeDriver();

    AppiumDriver getDriver();
}
