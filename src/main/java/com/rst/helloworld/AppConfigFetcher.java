package com.rst.helloworld;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

// POJO for your configuration
class AppConfig {
    public String dbUrl;
    public boolean featureFlag;
    public int maxConnections;

    @Override
    public String toString() {
        return "AppConfig [dbUrl=" + dbUrl + ", featureFlag=" + featureFlag + ", maxConnections=" + maxConnections + "]";
    }
}

public class AppConfigFetcher {

    private static final String CONFIG_URL = System.getenv("CONFIG_URL");
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static AppConfig fetchConfig() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CONFIG_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), AppConfig.class);
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            AppConfig config = fetchConfig();
            System.out.println("Fetched Config: " + config);
            Thread.sleep(5000); // Matches AppConfig agent poll interval
        }
    }
}
