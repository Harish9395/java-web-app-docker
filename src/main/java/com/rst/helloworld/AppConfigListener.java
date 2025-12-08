package com.rst.helloworld;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("AppConfig listener initialized...");
        DynamicAppConfig.startAutoRefresh(10);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("AppConfig listener destroyed...");
    }
}
