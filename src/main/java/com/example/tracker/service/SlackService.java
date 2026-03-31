package com.example.tracker.service;

import com.example.tracker.entity.Commit;

import java.util.List;

public interface SlackService {

    void sendNotification(String authorName, List<Commit> commits, String branch);
}