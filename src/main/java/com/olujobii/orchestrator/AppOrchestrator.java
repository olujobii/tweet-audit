package com.olujobii.orchestrator;

import com.google.gson.Gson;
import com.olujobii.ai_client.GeminiClient;
import com.olujobii.model.Criteria;
import com.olujobii.model.Tweet;
import com.olujobii.parser.CriteriaParser;
import com.olujobii.parser.TweetParser;

import java.io.IOException;
import java.util.List;

public class AppOrchestrator {
    private final Gson gson;
    private final TweetParser tweetParser;
    private final CriteriaParser criteriaParser;
    private final GeminiClient geminiClient;
    private final String filePath;
    private final String configPath;

    public AppOrchestrator(TweetParser tweetParser, CriteriaParser criteriaParser, GeminiClient geminiClient,
                           String filePath, String configPath) {
        this.gson = new Gson();
        this.tweetParser = tweetParser;
        this.criteriaParser = criteriaParser;
        this.geminiClient = geminiClient;
        this.filePath = filePath;
        this.configPath = configPath;
    }


    public void run () throws IOException {
        List<Tweet> tweets = tweetParser.readFile(gson, filePath);

        System.out.println("You have "+tweets.size()+" tweets to be analyzed");

        Criteria criteria = criteriaParser.readConfigFile(gson, configPath);

        //CHECK IF CRITERIA IS NULL
        if(criteria == null){
            System.out.println("All fields in the criteria.json file CANNOT be empty. At least a value for a field must be given");
            return;
        }
        System.out.println("Good to go");
    }
}
