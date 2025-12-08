import java.io.IOException;https://github.com/Harish9395/java-web-app-docker/tree/Feature-dev/src/main/java/com/rst/helloworld/config
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DynamicAppConfig: Fetches AWS AppConfig configuration dynamically via sidecar.
 * Auto-refreshes at a fixed interval.
 */
public class DynamicAppConfig {

    // Environment variables for sidecar
    private static final String APPCONFIG_HOST = System.getenv("APPCONFIG_HOST");
    private static final String APPCONFIG_PORT = System.getenv("APPCONFIG_PORT");
    private static final String APPCONFIG_APPLICATION = System.getenv("APPCONFIG_APPLICATION");
    private static final String APPCONFIG_ENVIRONMENT = System.getenv("APPCONFIG_ENVIRONMENT");
    private static final String APPCONFIG_CONFIGURATION = System.getenv("APPCONFIG_CONFIGURATION");

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static volatile Map<String, Object> configMap;

    /**
     * Constructs the sidecar URL dynamically.
     */
    private static String getConfigUrl() {
        return String.format(
                "http://%s:%s/applications/%s/environments/%s/configurations/%s",
                APPCONFIG_HOST,
                APPCONFIG_PORT,
                APPCONFIG_APPLICATION,
                APPCONFIG_ENVIRONMENT,
                APPCONFIG_CONFIGURATION
        );
    }

    /**
     * Fetches the latest configuration JSON from the sidecar.
     */
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

    /**
     * Returns the current configuration map.
     */
    public static Map<String, Object> getConfigMap() {
        return configMap;
    }

    /**
     * Returns a specific configuration value by key.
     */
    public static String get(String key) {
        Object value = configMap != null ? configMap.get(key) : null;
        return value != null ? value.toString() : null;
    }

    /**
     * Starts automatic refresh of configuration every `pollIntervalSeconds`.
     */
    public static void startAutoRefresh(int pollIntervalSeconds) {
        // Initial fetch
        refreshConfig();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(DynamicAppConfig::refreshConfig,
                pollIntervalSeconds, pollIntervalSeconds, TimeUnit.SECONDS);
    }

    /**
     * Simple demo showing how to use the class.
     */
    public static void main(String[] args) {
        // Start auto-refresh every 15 seconds (match sidecar poll interval)
        startAutoRefresh(15);

        // Demo loop
        while (true) {
            String myEnvVar = get("MY_ENV_VAR");
            System.out.println("Current MY_ENV_VAR: " + myEnvVar);
            try {
                Thread.sleep(5000); // Print every 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
