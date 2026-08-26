package com.olujobii.ai_client.impl;

import com.google.genai.errors.ApiException;
import com.olujobii.model.ModelResponseTweet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

class RetryHandler {
    private final RateLimiter rateLimiter;
    private final Random randomJitter;

    public RetryHandler(RateLimiter rateLimiter){
        this.rateLimiter = rateLimiter;
        this.randomJitter = new Random();
    }

    public List<ModelResponseTweet> retryMechanism(Supplier<List<ModelResponseTweet>> action) throws InterruptedException{
        final int maxAttempts = 5;
        final int baseDelay = 1000;

        List<ModelResponseTweet> tweets = new ArrayList<>();
        rateLimiter.checkLimit();
        for(int attempt = 0; attempt <= maxAttempts; attempt++) {
            try{
                tweets.addAll(action.get());
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
