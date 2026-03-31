package com.example.tracker.service;

import com.example.tracker.dto.GithubWebhookPayload;

import java.util.Map;

public interface GithubService {

    void processPayload(GithubWebhookPayload payload);
}