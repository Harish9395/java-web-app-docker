package com.example.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("AppConfig listener starting...");
        DynamicAppConfig.startAutoRefresh(10);
    }
}
