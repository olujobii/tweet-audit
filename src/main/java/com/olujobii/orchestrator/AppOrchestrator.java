package com.olujobii.orchestrator;

import com.google.genai.errors.ApiException;
import com.google.gson.Gson;
import com.olujobii.ai_client.GeminiClient;
import com.olujobii.ai_client.RateLimiter;
import com.olujobii.model.Criteria;
import com.olujobii.model.Tweet;
import com.olujobii.parser.CriteriaParser;
import com.olujobii.parser.TweetParser;
import com.olujobii.util.PromptBuilderUtil;

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


    public void run () throws IOException, InterruptedException {
        List<Tweet> tweets = tweetParser.readFile(gson, filePath);

        System.out.println("You have "+tweets.size()+" tweets to be analyzed");

        Criteria criteria = criteriaParser.readConfigFile(gson, configPath);

        //CHECK IF CRITERIA IS NULL
        if(criteria == null){
            System.out.println("All fields in the criteria.json file CANNOT be empty. At least a value for a field must be given");
            return;
        }


        boolean didExceptionOccur = false;
        int currentIndexWhenExceptionOccurred = 0;

        RateLimiter rateLimiter = new RateLimiter();
        for(int i = 0; i < tweets.size(); i++){
            try{
                //If a retry is happening, I want it to continue from where it stopped before it encountered the exception
                if(didExceptionOccur){
                    i = currentIndexWhenExceptionOccurred;
                    didExceptionOccur = false;
                }
                int tweetNumber = i + 1;
                System.out.printf("Analyzing %d/%d tweets.\n",tweetNumber, tweets.size());
                String prompt = PromptBuilderUtil.buildPrompt(criteria, tweets.get(i));
                geminiClient.analyzeTweet(prompt);
                //Little delay before moving on to next prompt.
                Thread.sleep(5000);
            }catch(ApiException ex){
                //Is it a Retryable error?
                if(ex.code() == 429 || ex.code() == 500 || ex.code() == 503 || ex.code() == 504){
                    //Have we reached our max number of retries?
                    if(rateLimiter.getAttempt() < rateLimiter.getMaxRetries()){
                        int waitTime = rateLimiter.getWaitTime();
                        rateLimiter.incrementAttemptCount();
                        didExceptionOccur = true;
                        currentIndexWhenExceptionOccurred = i;
                        Thread.sleep(waitTime);
                    }else {
                        throw new RuntimeException("Max number of retries reached, give it a couple of hours or try again after 24 hours.", ex);
                    }
                }else {
                    throw new RuntimeException("An error occurred", ex);
                }
            }
        }
    }
}
