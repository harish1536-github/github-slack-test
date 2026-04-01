package com.example.tracker.controller;

import com.example.tracker.dto.GithubWebhookPayload;
import com.example.tracker.service.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class GithubWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(GithubWebhookController.class);

    private final GithubService webhookService;

    /*
     * Constructor Injection
     * Spring sees WebhookService interface
     * automatically injects WebhookServiceImpl
     * because it is the only class that implements it
     */
    public GithubWebhookController(GithubService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/github")
    public ResponseEntity<String> receiveGitHubWebhook(
            @RequestHeader(value = "X-GitHub-Event", defaultValue = "unknown") String githubEvent,
            @RequestBody GithubWebhookPayload payload) {

        logger.info("📩 Received GitHub Event: {}", githubEvent);

        // Handle ping
        if ("ping".equals(githubEvent)) {
            logger.info("✅ Webhook connected successfully!");
            return ResponseEntity.ok("Pong! Webhook connected.");
        }

        if ("push".equals(githubEvent)) {

            if (payload == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payload is null");
            }

            if (payload.getCommits() == null || payload.getCommits().isEmpty()) {
                return ResponseEntity.ok("No commits to process");
            }

            try {
                webhookService.processPayload(payload);
                return ResponseEntity.ok("Webhook processed successfully");
            } catch (Exception e) {
                logger.error("❌ Error processing webhook: {}", e.getMessage());
                return ResponseEntity.ok("Webhook received but processing failed");
            }
        }

        return ResponseEntity.ok("Event type not handled");
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("✅ GitHub Slack Tracker is UP!");
    }
}