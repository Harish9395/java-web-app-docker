package com.rst.helloworld;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DynamicAppConfig {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static volatile Map<String, Object> configMap;

    private static String getConfigUrl() {
        return System.getenv("CONFIG_URL");
    }

    public static void refreshConfig() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getConfigUrl()))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                configMap = objectMapper.readValue(response.body(), Map.class);
                System.out.println("AppConfig updated: " + configMap);
            } else {
                System.err.println("Failed to fetch AppConfig: HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching AppConfig: " + e.getMessage());
        }
    }

    public static String get(String key) {
        Object value = configMap != null ? configMap.get(key) : null;
        return value != null ? value.toString() : null;
    }

    public static void startAutoRefresh(int pollIntervalSeconds) {
        refreshConfig();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(DynamicAppConfig::refreshConfig,
                pollIntervalSeconds, pollIntervalSeconds, TimeUnit.SECONDS);
    }
}
