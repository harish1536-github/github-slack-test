package com.example.tracker.service.impl;

import com.example.tracker.entity.Commit;
import com.example.tracker.service.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SlackServiceImpl implements SlackService {

    private static final Logger logger = LoggerFactory.getLogger(SlackServiceImpl.class);

    private final RestTemplate restTemplate;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    public SlackServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendNotification(String authorName, List<Commit> commits, String branch) {

        // Build Slack message
        StringBuilder message = new StringBuilder();
        message.append("🚀 *").append(authorName).append("*")
                .append(" pushed *").append(commits.size()).append(" commit(s)*")
                .append(" to branch *").append(branch).append("*\n\n");

        for (Commit commit : commits) {
            message.append("• ").append(commit.getMessage()).append("\n");
        }

        // Slack expects: { "text": "message" }
        String jsonBody = "{\"text\": \"" + escapeJson(message.toString()) + "\"}";

        try {
            restTemplate.postForObject(slackWebhookUrl, jsonBody, String.class);
            logger.info("Slack notification sent successfully");
        } catch (Exception e) {
            logger.error("Failed to send Slack notification: {}", e.getMessage());
        }
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}