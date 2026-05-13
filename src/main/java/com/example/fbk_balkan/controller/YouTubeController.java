package com.example.fbk_balkan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class YouTubeController {

    private static final String CHANNEL_ID = "UCAcquTaKhJu0resneu7e2_w";
    private static final String FEED_URL =
            "https://www.youtube.com/feeds/videos.xml?channel_id=" + CHANNEL_ID;

    private static final Pattern VIDEO_ID_PATTERN =
            Pattern.compile("<yt:videoId>([^<]+)</yt:videoId>");

    @GetMapping("/api/youtube/latest")
    public ResponseEntity<Map<String, String>> latestVideo() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(FEED_URL))
                    .timeout(Duration.ofSeconds(8))
                    .header("User-Agent", "Mozilla/5.0 (compatible; FBKBalkan/1.0)")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return ResponseEntity.status(502).body(Map.of("error", "upstream_error"));
            }

            Matcher matcher = VIDEO_ID_PATTERN.matcher(response.body());
            if (matcher.find()) {
                String videoId = matcher.group(1).trim();
                return ResponseEntity.ok(Map.of("videoId", videoId));
            }

            return ResponseEntity.status(404).body(Map.of("error", "no_videos_found"));

        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of("error", "fetch_failed"));
        }
    }
}
