package com.olujobii;

import com.google.genai.Client;
import com.google.genai.types.*;
import com.olujobii.model.Criteria;
import com.olujobii.model.Tweet;
import com.olujobii.util.StringBuilderUtil;

import java.util.List;
import java.util.Map;

public class GeminiClient {

    public void analyzeTweet(List<Tweet> tweets, Criteria criteria){

        //Building the alignment criteria and tweet to send as plain text to gemini
        String criteriaString = StringBuilderUtil.buildAlignmentCriteriaString(criteria);
        String tweet = StringBuilderUtil.buildTweetsString(tweets);


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
                            Part.fromText("Evaluate every tweet INDEPENDENTLY"),
                            Part.fromText("Flag a tweet if ANY of the conditions are true:"),
                            Part.fromText(criteriaString),
                            Part.fromText("LANGUAGE INTERPRETATION"),
                            Part.fromText("- Tweets may contain English, Nigerian Pidgin, slang, abbreviations or different Nigerian languages like Yoruba, Igbo or Hausa"),
                            Part.fromText("- Interpret intended meaning before applying rules"),
                            Part.fromText("- Evaluate context of tweet"),
                            Part.fromText("Return only tweets that violate. If no tweets violate, return an empty array"),
                            Part.fromText("Here are the tweets: "),
                            Part.fromText(tweet)
                            ),
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