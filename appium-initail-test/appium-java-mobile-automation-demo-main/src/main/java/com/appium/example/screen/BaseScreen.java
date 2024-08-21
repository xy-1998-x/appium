package com.appium.example.screen;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumBy;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static com.appium.example.constant.CommonConstants.MOBILE_PLATFORM_NAME;
import static com.appium.example.constant.DriverConstants.ANDROID;

public class BaseScreen {
    public final WebDriver driver;
    public final WebDriverWait wait;

    public BaseScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void waitUntilElementVisible(By by) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }


    //滚动找文本
    public WebElement scrollToElement(String elementText) {
        WebElement element;

        if (MOBILE_PLATFORM_NAME.equalsIgnoreCase(ANDROID)) {
            element = driver
                    .findElement(
                            AppiumBy.androidUIAutomator(
                                    "new UiScrollable(new UiSelector().scrollable(true))"
                                            + ".scrollIntoView(new UiSelector().text(\"" + elementText + "\"))"
                            )
                    );
        } else {
            element = driver.findElement(AppiumBy.iOSNsPredicateString("label == '" + elementText + "'"));
        }

        return element;
    }

    public void tap(By by) {
        waitUntilElementVisible(by);
        driver.findElement(by).click();
    }

    public void scrollAndTap(String elementText) {
        scrollToElement(elementText).click();
    }

    public void inputText(By by, String text) {
        waitUntilElementVisible(by);
        driver.findElement(by).sendKeys(text);

    }

    public void scrollAndInputText(String elementText, String text) {
        scrollToElement(elementText).sendKeys(text);
    }

    //上滑
    public void scrolldown(String n) throws InterruptedException {
//        waitUntilElementVisible(by);
        long startTime = System.currentTimeMillis();

        for(int i = 0; i <= Integer.parseInt(n); i++)
//        while ((System.currentTimeMillis() - startTime) < 10000)
        {
            Dimension size = driver.manage().window().getSize();
        int X = size.width / 2;
        int Y = (int) (size.height * 0.2);
        ((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", ImmutableMap.of(
                "left", 10, "top", 10, "width", X, "height", Y,
                "direction", "up",
                "percent", 0.75
        ));

        Thread.sleep(2000);
        }
    }

    //右滑
    public void scrollright(String n) throws InterruptedException {
//        waitUntilElementVisible(by);
        long startTime = System.currentTimeMillis();

        for(int i = 0; i <= Integer.parseInt(n); i++)
//        while ((System.currentTimeMillis() - startTime) < 10000)
        {
            Dimension size = driver.manage().window().getSize();
            int X = size.width/2 ;
            int Y = (int) (size.height * 0.2);
            ((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", ImmutableMap.of(
                    "left", 10, "top", 10, "width", X, "height", Y,
                    "direction", "left",
                    "percent", 0.75
            ));

            Thread.sleep(2000);
        }
    }

    //找元素
    public void findtext(String targetText) throws InterruptedException {

      if(  driver.findElement(By.xpath("//*[@text='"+targetText+"']") )== null)
        {
            Dimension size = driver.manage().window().getSize();
            int X = size.width/2 ;
            int Y = (int) (size.height * 0.2);
            ((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", ImmutableMap.of(
                    "left", 10, "top", 10, "width", X, "height", Y,
                    "direction", "left",
                    "percent", 0.75
            ));

            Thread.sleep(2000);
        }
      else{
          Thread.sleep(5000);
      }

    }

    public void screenshot(String yourpath,String appname,String taskname) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        String path = yourpath;
        String folderName = appname;
//        String path = "C:\\Users\\86158\\Desktop\\";
//        String folderName = "pipixia";

        File folder = new File(path + folderName);
        try {
            if (!folder.exists()) {
                boolean created = folder.mkdirs();  //创建以该路劲名命名的目录 文件夹
                if (created) {
                    System.out.println("文件夹创建成功：" + folder.getAbsolutePath());
                } else {
                    System.out.println("文件夹创建失败。");
                }
            } else {
                System.out.println("文件夹已存在：" + folder.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        File destinationFile = new File(folder, taskname+".png");
        FileUtils.copyFile(screenshot, destinationFile);

    }





}
