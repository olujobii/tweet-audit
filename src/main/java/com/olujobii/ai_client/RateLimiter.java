package com.olujobii.ai_client;

import com.google.genai.errors.ApiException;
import com.olujobii.model.ModelResponseTweet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RateLimiter{
    private final int maxAttempts;
    private final int baseDelay;
    private final AIProvider aiProvider;
    private final Random randomJitter;

    public RateLimiter(int maxAttempts, int baseDelay, AIProvider aiProvider){
        this.maxAttempts = maxAttempts;
        this.baseDelay = baseDelay;
        this.aiProvider = aiProvider;
        this.randomJitter = new Random();
    }


    public List<ModelResponseTweet> callAIProvider(String prompt) throws InterruptedException{
        List<ModelResponseTweet> tweets = new ArrayList<>();

        for(int attempt = 0; attempt < maxAttempts; attempt++) {
            try{
                tweets.addAll(aiProvider.analyzeTweets(prompt)); //If this throws an exception, we handle it here
                break;
            }catch (ApiException ex){
                if((ex.code() == 429 || ex.code() == 500 || ex.code() == 503 || ex.code() == 504) && (attempt != maxAttempts - 1)){
                    int delay = baseDelay * (int) Math.pow(2,attempt);
                    int finalDelay = delay + randomJitter.nextInt(500,1100);
                    Thread.sleep(finalDelay);
                }else{
                    throw new RuntimeException(ex);
                }
            }catch(RuntimeException ex){
                throw new RuntimeException(ex);
            }
        }
        return tweets;
    }
}
