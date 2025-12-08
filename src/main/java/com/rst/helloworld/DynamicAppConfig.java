package com.rst.helloworld;

import java.net.URI;
import java.net.http.*;
import java.util.Map;
import java.util.concurrent.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DynamicAppConfig {

    private static volatile Map<String, Object> configMap;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void refreshConfig() {
        try {
            String url = System.getenv("CONFIG_URL");
            if (url == null) {
                System.err.println("CONFIG_URL not set");
                return;
            }

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> res =
                    client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                configMap = mapper.readValue(res.body(), Map.class);
                System.out.println("[AppConfig] Updated: " + configMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        if (configMap == null) return null;
        Object value = configMap.get(key);
        return value == null ? null : value.toString();
    }

    public static void startAutoRefresh(int seconds) {
        refreshConfig();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(DynamicAppConfig::refreshConfig,
                seconds, seconds, TimeUnit.SECONDS);
    }
}
