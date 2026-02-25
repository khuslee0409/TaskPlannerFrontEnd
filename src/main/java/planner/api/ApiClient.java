package planner.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private final String baseUrl;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public <T> T postJson(String path, Object body, Class<T> responseType, String bearerToken)
            throws IOException, InterruptedException {

        String json = mapper.writeValueAsString(body);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(json));

        if (bearerToken != null && !bearerToken.isBlank()) {
            builder.header("Authorization", "Bearer " + bearerToken);
        }

        HttpResponse<String> res = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + ": " + res.body());
        }

        if (responseType == Void.class) {
            return null;
        }

        return mapper.readValue(res.body(), responseType);
    }

    public <T> T getJson(String path, Class<T> responseType, String bearerToken)
            throws IOException, InterruptedException {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(60))
                .GET();

        if (bearerToken != null && !bearerToken.isBlank()) {
            builder.header("Authorization", "Bearer " + bearerToken);
        }

        HttpResponse<String> res = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + res.statusCode() + ": " + res.body());
        }

        return mapper.readValue(res.body(), responseType);
    }

    public <T> T putJson(String path, Object body, Class<T> responseType, String bearerToken)
        throws IOException, InterruptedException {

    String json = mapper.writeValueAsString(body);

    HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(60))
            .PUT(HttpRequest.BodyPublishers.ofString(json));  // ← PUT not POST!

    if (bearerToken != null && !bearerToken.isBlank()) {
        builder.header("Authorization", "Bearer " + bearerToken);
    }

    HttpResponse<String> res = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    if (res.statusCode() / 100 != 2) {
        throw new RuntimeException("HTTP " + res.statusCode() + ": " + res.body());
    }

    if (responseType == Void.class) {
        return null;
    }

    return mapper.readValue(res.body(), responseType);
}
}
