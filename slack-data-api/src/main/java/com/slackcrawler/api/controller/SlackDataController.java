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
}

