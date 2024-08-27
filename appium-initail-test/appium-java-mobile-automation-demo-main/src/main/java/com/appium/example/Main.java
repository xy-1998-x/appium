/**
 * @authors xiny
 */

package com.appium.example;

import com.appium.example.bean.AllTask;
import com.appium.example.bean.AppInfo;
import com.appium.example.bean.Step;
import com.appium.example.bean.Task;
import com.appium.example.constant.FindType;
import com.appium.example.constant.OperateType;
import com.appium.example.screen.BaseScreen;
import com.appium.example.util.driver.MobileDriverFactory;
import com.appium.example.util.driver.MobileDriverHolder;
import com.appium.example.util.driver.MobileDriverService;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.appium.example.util.driver.MobileDriverHolder.setDriver;
//import static com.sun.tools.javac.jvm.PoolConstant.LoadableConstant.Int;

public class Main {
    private static final Logger logger = LogManager.getLogger();

    private void generateTask(Path appNamePath, AllTask allTask) {
        String appName = appNamePath.getFileName().toString();
        allTask.setAppList(appName);

        try (Stream<Path> tasksPath = Files.list(appNamePath)) {
            tasksPath.forEach(taskPath -> {
                String taskName = taskPath.getFileName().toString();
                System.out.println("文件: " + taskName);
                Task task = new Task();
                task.setAppName(appName);
                task.setTaskName(taskName);
                allTask.setTask(appName, task);
                try {
                    Yaml yaml = new Yaml();
                    InputStream inputStream = new FileInputStream(taskPath.toFile());
                    ArrayList<HashMap<String, String>> arrayList = yaml.loadAs(inputStream, ArrayList.class);
                    arrayList.forEach(ele -> {
                        Step step = new Step();
                        if (ele.get("app_name") != null) {
                            step.setAppName(ele.get("app_name"));
                        }

                        if (ele.get("activity_name") != null) {
                            step.setActivityName(ele.get("activity_name"));
                        }
                        step.setNumsinfo((ele.get("num_info")));
                        step.setElementInfo(ele.get("element_info"));
                        step.setFindType(FindType.valueOf(ele.get("find_type")));// get一个find_type的值 并使用valueOf将其转化为FindType的枚举类型
                        OperateType operateType = OperateType.valueOf(ele.get("operate_type"));
                        step.setOperateType(operateType);
                        if (operateType.equals(OperateType.INPUT)) {
                            step.setInputText(ele.get("key"));
                        }
                        task.setSteps(step);
                    });
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AllTask generateTask() throws Exception {
        AllTask allTask = new AllTask();
        URL resource = Main.class.getClassLoader().getResource("tasks");
        if (resource == null) {
            System.out.println("task目录不存在");
            return null;
        }
        Path taskDir = Paths.get(resource.toURI());
        if (!Files.isDirectory(taskDir)) {
            System.out.println("task不是一个目录");
            return null;
        }
        try (Stream<Path> paths = Files.list(taskDir)) {
            paths.filter(Files::isDirectory)
                    .forEach(appNamePath -> {
                        generateTask(appNamePath, allTask);
                        String appName = appNamePath.getFileName().toString();
                        System.out.println("目录: " + appName);
                    });
        }
        return allTask;
    }

    public void afterExec(MobileDriverService driverService) {
        driverService.closeDriver();
    }

    public MobileDriverService beforeExec(String appName, String activityName) {
        MobileDriverService driverService = new MobileDriverFactory().getDriverService();
        AppInfo appInfo = AppInfo.builder()
                .androidAppActivity(activityName)
//                .androidAppFilePath("/Users/zhu/github/appium-java-mobile-automation-demo/src/test/resources/apps/dev/wdioNativeDemoApp.apk")
                .androidAppPackage(appName)
                .build();
        driverService.initAppInfo(appInfo);
        AppiumDriverLocalService appiumService = driverService.startAppiumService();
        driverService.spinUpDriver(appiumService);
        setDriver(driverService.getDriver());
        return driverService;
    }

    public void execTask(Task task) throws IOException {
        BaseScreen baseScreen = new BaseScreen(MobileDriverHolder.getDriver());
        // appium定位元素的几种方式：https://blog.csdn.net/lovedingd/article/details/111058898
        // https://cloud.tencent.com/developer/article/1816977
        List<Step> steps = task.getSteps();
        steps.forEach(step -> {
            By elementInfo = null;
            String info = step.getElementInfo();
            switch (step.getFindType()) {
                //elementInfo是当前页面上第一个匹配的元素WebElement类型
                case ID -> elementInfo = AppiumBy.id(info);
                case CLASSNAME -> elementInfo = AppiumBy.className(info);
                case DESC -> elementInfo = AppiumBy.accessibilityId(info);
                case XPATH -> elementInfo = AppiumBy.xpath(info);
                default -> {
                    System.out.println("请检查元素查找类型，当前仅支持 'id/classname/content-desc/xpath'");
                    return;
                }
            }
            switch (step.getOperateType()) {
                ///*********
                case SCROLLTOTEXT -> baseScreen.scrollToElement("");
                case DOUBLECLICK -> baseScreen.doubleclick(elementInfo);
                case CLICK -> baseScreen.tap(elementInfo);
                case INPUT -> baseScreen.inputText(elementInfo, step.getInputText());
                case SCROLLDOWN -> {
                    try {
                        baseScreen.scrolldown(step.getNumsinfo());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                case SCROLLRIGHT -> {
                    try {
                        baseScreen.scrollright(step.getNumsinfo());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                default -> System.out.println("请检查操作类型，当前操作类型包括 '点击/文本输入'");
            }
        });

//        try {
//            // 这里不会有异常抛出，但只是为了和 finally 配合使用
//        } catch (Exception e) {
//            // 这里不会执行到
//        } finally {
//            baseScreen.screenshot();
//
//        }

    }

    public static void main(String[] args) throws Exception {

        int index = 3;

        Main main = new Main();
        Path taskpath = Path.of("C:\\Users\\86158\\Desktop\\test\\测试.yaml");

        String yourpath="C:\\Users\\86158\\Desktop\\";

        AllTask allTask = main.generateTask();
        List<String> appList = allTask.getAppList();


        Task taskyaml = new Task();

        //解析yaml文件
      //  Step steplist = new Step();  不要放外外面 因为如果放外面 没办法每次循环得时候初始化 导致不能list step
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(taskpath.toFile());
            ArrayList<HashMap<String, String>> arrayList = yaml.loadAs(inputStream, ArrayList.class);

            arrayList.forEach(ele -> {
                Step steplist = new Step();     //这个放在里面就可以把step列出来 而如果放在外米娜就不行了 放里面 每次更新这个
                if (ele.get("app_name") != null) {
                    steplist.setAppName(ele.get("app_name"));
                }

                //解析yaml文件并保存为对应的字符串数组
                if (ele.get("taskslist") != null) {
                    steplist.setTaskslist(ele.get("taskslist"));
                }

                // //每次循环都在更新同一个Step对象steplist，这意味着最终taskyaml.setSteps(steplist)设置的只是最后一个 “step” 的信息，而不是所有 “step” 的集合。
                taskyaml.setSteps(steplist);
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }



        Step temp =  taskyaml.getSteps().get(index);

        String appstr = temp.getAppName();
        String taskstr = temp.getTaskslist();

       // String appstr = steplist.getAppName();
      //  String taskstr = steplist.getTaskslist();
        String[] parts = taskstr.split(",");

        appList.forEach(app -> {
            if (app.equals(appstr)){

                //不同的任务队列tasklist进行for循环
        for(int i=0;i < parts.length;i++) {
            List<Task> taskByApp = allTask.getTask(app);
            MobileDriverService mobileDriverService = main.beforeExec(taskByApp.get(0).getSteps().get(0).getAppName(), taskByApp.get(0).getSteps().get(0).getActivityName());

            String part = parts[i];
            String[] partlist = part.split("\\+");
            //把他变成一个链表  .map操作后的 Stream（其中的字符串都加上了.yaml后缀）收集到一个新的List<String>中。
            List<String> newPartListAsList = Arrays.stream(partlist)
                    .map(str -> str + ".yaml")
                    .collect(Collectors.toList());

                    //这个 for 循环是轮询执行解析出来的listtask中的task
                    for (int j = 0; j < newPartListAsList.size(); j++) {
                        //不同的去和tasks比
                        for (int k = 0; k < taskByApp.size(); k++) {
                            Task task = taskByApp.get(k);

                            //将在tasklist中取得的task与解析的进行比较
                            if (task.getTaskName().equals(newPartListAsList.get(j))) {

                                try {
                                    main.execTask(task);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } finally {
                                    BaseScreen baseScreen = new BaseScreen(MobileDriverHolder.getDriver());
                                    try {

                                        baseScreen.screenshot(yourpath, task.getAppName(), task.getTaskName());

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                            }   //if的
                        }

                    }   //for的

            try {
                main.afterExec(mobileDriverService);
            }  finally {
                if (i != parts.length-1) {
                    System.out.println("还有任务没有执行完毕'");
                } else {
                    System.out.println("app任务队列全部执行完毕'");
                }

            }

        }  //第一个for
    }//if (app.equals(appstr)){
});

    }



}
