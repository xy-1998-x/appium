package com.appium.example.bean;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllTask {

    private static final List<String> appList = new ArrayList<>();
    private static final Map<String, List<Task>> app2Task = HashMap.newHashMap(16);

    public void setAppList(String appName) {
        appList.add(appName);
    }

    public List<String> getAppList() {
        return appList;
    }

    public List<Task> getTask(String appName) {
        return app2Task.get(appName);
    }

    public void setTask(String appName, Task task) {
        List<Task> tasks;
        if (app2Task.get(appName) == null) {
            tasks = new ArrayList<>();
            app2Task.put(appName, tasks);
        } else {
            tasks = app2Task.get(appName);
        }
        tasks.add(task);
    }
}
