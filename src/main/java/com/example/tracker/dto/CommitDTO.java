package com.example.tracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitDTO {

    private String id;           // commit SHA
    private String message;      // commit message
    private String timestamp;    // when it was made
    private CommitAuthorDTO author; // who made it
}