package com.olujobii.ai_client;

import com.google.genai.errors.ApiException;
import com.olujobii.model.ModelResponseTweet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExponentialBackoff {
    private final AIProvider aiProvider;
    private final Random randomJitter;

    public ExponentialBackoff(AIProvider aiProvider){
        this.aiProvider = aiProvider;
        this.randomJitter = new Random();
    }

    public List<ModelResponseTweet> callAIProvider(String prompt) throws InterruptedException{
        final int maxAttempts = 5;
        final int baseDelay = 1000;

        List<ModelResponseTweet> tweets = new ArrayList<>();
        for(int attempt = 0; attempt <= maxAttempts; attempt++) {
            try{
                tweets.addAll(aiProvider.analyzeTweets(prompt)); //If this throws an exception, we handle it here
                break;
            }catch (ApiException ex){
                if((ex.code() == 429 || ex.code() == 500 || ex.code() == 503) && (attempt != maxAttempts)){
                    int delay = baseDelay * (int) Math.pow(2,attempt);
                    int finalDelay = delay + randomJitter.nextInt(500,1100);
                    Thread.sleep(finalDelay);
                }else{
                    throw ex;
                }
            }
        }
        return tweets;
    }
}
