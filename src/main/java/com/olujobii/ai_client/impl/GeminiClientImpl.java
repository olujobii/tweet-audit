package com.olujobii.ai_client.impl;

import com.google.common.reflect.TypeToken;
import com.google.genai.Client;
import com.google.genai.types.*;
import com.google.gson.Gson;
import com.olujobii.ai_client.AIProvider;
import com.olujobii.model.ModelResponseTweet;
import com.olujobii.model.Reason;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeminiClientImpl implements AIProvider {
    private static final String MODEL = "gemini-3.1-flash-lite";
    private final RetryHandler retryHandler;

    public GeminiClientImpl(int requestPerMinute){
        this.retryHandler = new RetryHandler(new RateLimiter(requestPerMinute));
    }

    @Override
    public List<ModelResponseTweet> analyzeTweets(String prompt){
        List<ModelResponseTweet> tweets;
        try{
            tweets = new ArrayList<>(retryHandler.retryMechanism(() -> sendPrompt(prompt)));
        }catch(InterruptedException ex){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Tweet Analysis Interrupted :", ex);
        }
        return tweets;
    }

    private List<ModelResponseTweet> sendPrompt(String prompt){
        Gson gson = new Gson();
        List<ModelResponseTweet> modelResponseTweets;
        java.lang.reflect.Type type = new TypeToken<List<ModelResponseTweet>>(){}.getType();

        Schema jsonObject = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "id",Schema.builder().type(Type.Known.STRING).description("Represents the tweet id").build(),
                        "isFlagged",Schema.builder().type(Type.Known.BOOLEAN).description("This indicates whether the tweet is flagged or not").build(),
                        "tweet",Schema.builder().type(Type.Known.STRING).description("Represents the tweet content").build(),
                        "classification",Schema.builder().type(Type.Known.STRING)
                                .enum_(Reason.DISRESPECTFUL.getValue(), Reason.FORBIDDEN_WORDS.getValue(),
                                        Reason.UNPROFESSIONAL.getValue(), Reason.POLITICAL.getValue(),
                                        Reason.NO_ISSUE.getValue())
                                .description("Must be exactly one of the predefined values. If the tweet is not flagged, it should be No issue.").build()
                ))
                .required("id","tweet","classification","isFlagged")
                .build();

        Schema responseArrayObject = Schema.builder()
                .type(Type.Known.ARRAY)
                .description("The list of tweets analyzed returning all required properties of the tweet object")
                .items(jsonObject)
                .build();

        try(Client client = new Client()){
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    .responseJsonSchema(responseArrayObject)
                    .thinkingConfig(ThinkingConfig.builder()
                            .thinkingLevel(ThinkingLevel.Known.LOW)
                            .build())
                    .build();

            GenerateContentResponse response = client.models.generateContent(
                    MODEL,
                    prompt,
                    config
            );

            modelResponseTweets = gson.fromJson(response.text(), type);

            return modelResponseTweets;
        }
    }
}
