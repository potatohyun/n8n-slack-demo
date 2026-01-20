package com.slackcrawler.api.controller;

import com.slackcrawler.api.dto.ApiResponse;
import com.slackcrawler.api.dto.SlackDataRequest;
import com.slackcrawler.api.service.SlackDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/slack")
public class SlackDataController {

    private final SlackDataService slackDataService;

    public SlackDataController(SlackDataService slackDataService) {
        this.slackDataService = slackDataService;
    }

    @PostMapping("/data")
    public ResponseEntity<ApiResponse> receiveSlackData(@RequestBody SlackDataRequest request) {
        try {
            String fileName = slackDataService.saveSlackData(request);
            return ResponseEntity.ok(new ApiResponse(true, "Slack data saved successfully", fileName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error saving slack data: " + e.getMessage(), null));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/timestamps/{channelId}")
    public ResponseEntity<ApiResponse> getLastTimestamp(@PathVariable String channelId) {
        try {
            String timestamp = slackDataService.getLastTimestamp(channelId);
            return ResponseEntity.ok(new ApiResponse(true, "Timestamp retrieved successfully", timestamp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving timestamp: " + e.getMessage(), null));
        }
    }

    @PutMapping("/timestamps/{channelId}")
    public ResponseEntity<ApiResponse> saveLastTimestamp(
            @PathVariable String channelId,
            @RequestBody java.util.Map<String, String> request) {
        try {
            String timestamp = request.get("timestamp");
            // 빈 timestamp는 무시하고 성공 응답 반환
            if (timestamp == null || timestamp.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse(true, "No timestamp to save (skipped)", null));
            }
            slackDataService.saveLastTimestamp(channelId, timestamp);
            return ResponseEntity.ok(new ApiResponse(true, "Timestamp saved successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error saving timestamp: " + e.getMessage(), null));
        }
    }
}

