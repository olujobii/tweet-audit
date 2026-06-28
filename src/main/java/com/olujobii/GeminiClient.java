package com.olujobii;

import com.google.genai.Client;
import com.google.genai.types.*;
import com.google.gson.Gson;
import com.olujobii.model.Criteria;
import com.olujobii.model.Tweet;

import java.util.List;
import java.util.Map;

public class GeminiClient {

    public void analyzeTweet(Gson gson, List<Tweet> tweets, Criteria criteria){
        String criteriaJson = gson.toJson(criteria);
        String tweetsJson = gson.toJson(tweets);

        Schema objectSchema = Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "flaggedTweet", Schema.builder().type(Type.Known.STRING).description("Flagged tweet content from prompt").build(),
                        "id", Schema.builder().type(Type.Known.STRING).description("ID of the tweet from prompt").build(),
                        "reason", Schema.builder().type(Type.Known.STRING).description("Gives a summary of the criteria it failed (Not more than 5 words)").build()
                ))
                .required("flaggedTweet","id","reason")
                .build();

        Schema arraySchema = Schema.builder()
                .type(Type.Known.ARRAY)
                .description("An array that contains object of flagged tweets")
                .items(objectSchema)
                .build();

        try(Client client = new Client()){

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .systemInstruction(Content.fromParts(
                            Part.fromText("You are a twitter analyzer expert who is deeply specialized in analyzing and " +
                                    "flagging inappropriate tweets that go against the criteria provided. Below is the criteria: "),
                            Part.fromText(criteriaJson)
                    ))
                    .responseMimeType("application/json")
                    .responseJsonSchema(arraySchema)
                    .build();

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash-lite",
                    Content.fromParts(
                            Part.fromText("Analyze these tweets given to you. I want you to flag tweets that go against the criteria the has been passed in your system instruction: "),
                            Part.fromText(tweetsJson)),
                    config
            );

            System.out.println(response.text());
        }
    }
}
