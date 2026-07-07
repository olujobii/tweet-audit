package com.olujobii.ai_client;

import com.google.genai.Client;
import com.google.genai.types.*;

import java.util.Map;

public class GeminiClient {
    private static final String MODEL = "gemini-3.1-flash-lite";

    public void analyzeTweet(String prompt){

        Schema responseJsonObject = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "id",Schema.builder().type(Type.Known.STRING).description("Represents the tweet id").build(),
                        "tweet",Schema.builder().type(Type.Known.STRING).description("Represents the tweet content").build(),
                        "reason",Schema.builder().type(Type.Known.STRING).description("Represents the reason for your decision. Should be concise and short").build(),
                        "isFlagged",Schema.builder().type(Type.Known.BOOLEAN).description("This indicates whether the tweets is flagged or not").build()))
                .required("id","tweet","reason","isFlagged")
                .build();
        try(Client client = new Client()){
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    .responseJsonSchema(responseJsonObject)
                    .build();

            GenerateContentResponse response = client.models.generateContent(
                    MODEL,
                    prompt,
                    config
            );

            System.out.println(response.text());
        }
    }
}
