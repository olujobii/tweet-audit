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

        //CONVERTING LISTS OF TWEET TO A STRING
        //FIXME: Can put this in a util package
//        StringBuilder sb = new StringBuilder();
//        for(int i = 49 ; i < 100; i++){
//            int tweetNumber = i + 1;
//            sb.append("[Tweet ").append(tweetNumber).append("]\n");
//            sb.append("ID: ").append(tweets.get(i).id()).append("\n");
//            sb.append("Text: ").append(tweets.get(i).full_text()).append("\n");
//        }
//
//        String tweet = sb.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("[TWEET 1]");
        sb.append("Text: ").append("I love pizza");
        sb.append("ID: ").append("3231233121");

        sb.append("[TWEET 2]");
        sb.append("Text: ").append("Fuck you");
        sb.append("ID: ").append("341323214");

        sb.append("[TWEET 3]");
        sb.append("Text: ").append("I hate you");
        sb.append("ID: ").append("3423434");

String tweet = sb.toString();
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
                    .responseMimeType("application/json")
                    .responseJsonSchema(arraySchema)
                    .build();

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash-lite",
                    Content.fromParts(
                            Part.fromText("Evaluate the following tweets using the moderation criteria:"),
                            Part.fromText("Flag hate speech"),
                            Part.fromText("INSTRUCTIONS"),
                            Part.fromText("1. Read through all tweets provided."),
                            Part.fromText("2. Be sure to evaluate each tweet individually, leave no single tweet out during evaluation."),
                            Part.fromText("3. Flag tweet that violates the moderation criteria used as a guide."),
                            Part.fromText("4. If none violate criteria, return []"),
                            Part.fromText("5. Do not return compliant tweets"),
                            Part.fromText("Here are the tweets for evaluation: "),
                            Part.fromText(tweet)),
                    config
            );
            System.out.println(response.text());
            checkAPIUsage(response);
        }
    }

    private void checkAPIUsage(GenerateContentResponse response){
        if(response.usageMetadata().isPresent()){
            System.out.println("API USAGE");
            var usage = response.usageMetadata().get();

            System.out.println("Prompt token used: "+(usage.promptTokenCount().isPresent() ? usage.promptTokenCount().get() : "not present"));
            System.out.println("Candidates (Output) token used: "+(usage.candidatesTokenCount().isPresent() ? usage.candidatesTokenCount().get() : "not present"));
        }
    }
}

//EXAMPLE TO TEST STATIC CRITERIA - WHICH WORKED
//StringBuilder sb = new StringBuilder();
//        sb.append("[TWEET 1]");
//        sb.append("Text: ").append("I love pizza");
//        sb.append("ID: ").append("3231233121");
//
//        sb.append("[TWEET 2]");
//        sb.append("Text: ").append("Fuck you");
//        sb.append("ID: ").append("341323214");
//
//        sb.append("[TWEET 3]");
//        sb.append("Text: ").append("I hate you");
//        sb.append("ID: ").append("3423434");
//
//String tweet = sb.toString();