package com.olujobii;

import com.google.gson.Gson;
import com.olujobii.model.Criteria;
import com.olujobii.model.Tweet;

import java.io.IOException;
import java.util.List;

public class Orchestrator {
    private final Gson gson;
    private final TweetHandler tweetHandler;
    private final CriteriaHandler criteriaHandler;
    private final GeminiClient geminiClient;
    private final String filePath;
    private final String configPath;

    public Orchestrator(TweetHandler tweetHandler, CriteriaHandler criteriaHandler, GeminiClient geminiClient,
                        String filePath, String configPath) {
        this.gson = new Gson();
        this.tweetHandler = tweetHandler;
        this.criteriaHandler = criteriaHandler;
        this.geminiClient = geminiClient;
        this.filePath = filePath;
        this.configPath = configPath;
    }


    public void run () throws IOException {
        List<Tweet> tweets = tweetHandler.readFile(gson, filePath);

        System.out.println("You have "+tweets.size()+" tweets to be analyzed");

        Criteria criteria = criteriaHandler.readConfigFile(gson, configPath);

        System.out.println(String.join(", ",criteria.forbiddenWords()));

        geminiClient.analyzeTweet(gson, tweets, criteria);
    }
}
