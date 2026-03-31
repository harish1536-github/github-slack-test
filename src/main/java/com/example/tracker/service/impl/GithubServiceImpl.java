package com.example.tracker.service.impl;



import com.example.tracker.dto.CommitDTO;
import com.example.tracker.dto.GithubWebhookPayload;
import com.example.tracker.entity.Author;
import com.example.tracker.entity.Commit;
import com.example.tracker.repository.AuthorRepository;
import com.example.tracker.repository.CommitRepository;
import com.example.tracker.service.GithubService;
import com.example.tracker.service.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GithubServiceImpl implements GithubService {

    private static final Logger logger = LoggerFactory.getLogger(GithubServiceImpl.class);

    private final AuthorRepository authorRepository;
    private final CommitRepository commitRepository;
    private final SlackService slackService;

    /*
     * Constructor Injection
     */
    public GithubServiceImpl(AuthorRepository authorRepository,
                              CommitRepository commitRepository,
                              SlackService slackService) {
        this.authorRepository = authorRepository;
        this.commitRepository = commitRepository;
        this.slackService = slackService;
    }

    @Override
    public void processPayload(GithubWebhookPayload payload) {

        // STEP 1 — Save or fetch Author
        Author author = saveOrFetchAuthor(payload);
        logger.info("👤 Author: {}", author.getName());

        // STEP 2 — Save Commits
        List<Commit> savedCommits = saveCommits(payload, author);
        logger.info("💾 Saved {} commit(s)", savedCommits.size());

        // STEP 3 — Send Slack notification
        slackService.sendNotification(author.getName(), savedCommits, payload.getBranchName());
    }

    private Author saveOrFetchAuthor(GithubWebhookPayload payload) {

        String pusherName = payload.getPusher().getName();
        String pusherEmail = payload.getPusher().getEmail();

        return authorRepository.findByName(pusherName)
                .orElseGet(() -> {
                    Author newAuthor = new Author();
                    newAuthor.setName(pusherName);
                    newAuthor.setEmail(pusherEmail);
                    return authorRepository.save(newAuthor);
                });
    }

    private List<Commit> saveCommits(GithubWebhookPayload payload, Author author) {

        List<Commit> savedCommits = new ArrayList<>();
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");


        for (CommitDTO commitDTO : payload.getCommits()) {

            // Skip duplicates
            if (commitRepository.existsByCommitId(commitDTO.getId())) {
                logger.warn("⚠️ Commit already exists, skipping: {}", commitDTO.getId());
                continue;
            }

            Commit commit = new Commit();
            commit.setCommitId(commitDTO.getId());
            commit.setMessage(commitDTO.getMessage());
            String formattedTimestamp;
            try {
                LocalDateTime dateTime = LocalDateTime.parse(commitDTO.getTimestamp(), inputFormatter);
                formattedTimestamp = dateTime.format(outputFormatter);
            } catch (Exception e) {
                logger.error("❌ Error parsing timestamp: {}", commitDTO.getTimestamp(), e);
                formattedTimestamp = commitDTO.getTimestamp(); // fallback
            }
            commit.setCommitterName(commitDTO.getAuthor().getName());
            commit.setAuthor(author);

            Commit saved = commitRepository.save(commit);
            savedCommits.add(saved);
            logger.info("✅ Saved commit: {}", commit.getMessage());
        }

        return savedCommits;
    }
}