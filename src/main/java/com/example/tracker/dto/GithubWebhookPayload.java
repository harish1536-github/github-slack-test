package com.example.tracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubWebhookPayload {

    private String ref;              // "refs/heads/main"
    private PusherDTO pusher;        // who pushed
    private List<CommitDTO> commits; // list of commits

    // extracts "main" from "refs/heads/main"
    public String getBranchName() {
        if (ref != null && ref.contains("/")) {
            return ref.substring(ref.lastIndexOf("/") + 1);
        }
        return ref;
    }
}

