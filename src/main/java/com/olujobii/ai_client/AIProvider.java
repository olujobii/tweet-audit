package com.olujobii.ai_client;

import com.olujobii.model.ModelResponseTweet;

import java.util.List;

public interface AIProvider {

    List<ModelResponseTweet> analyzeTweets(String prompt);
}
