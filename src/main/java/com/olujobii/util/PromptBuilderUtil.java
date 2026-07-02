package com.olujobii.util;

import com.olujobii.model.Criteria;
import com.olujobii.model.Tweet;

public class PromptBuilderUtil {

    public static String buildPrompt(Criteria criteria, Tweet tweet){
        StringBuilder sb = new StringBuilder();
            sb.append("MODERATION RULES\n");
            sb.append("Flag this tweet if any of the conditions are true:\n");

            if(criteria.forbiddenWords() != null && !criteria.forbiddenWords().isEmpty()) {
                sb.append("\nFORBIDDEN WORDS\n");
                sb.append("Flag tweet if it contains any of these words:\n");
                criteria.forbiddenWords().forEach(word -> sb.append(word).append("\n"));
            }

            if(criteria.professionalCheck()){
                sb.append("\nPROFESSIONALISM\n");
                sb.append("Flag tweet if it is unprofessional.\n");
                sb.append("Examples of tweets that appear unprofessional are:\n");
                sb.append("Insults\n");
                sb.append("Aggressive language\n");
                sb.append("Racism and discrimination\n");
                sb.append("Offensive remarks\n");
            }

            if(criteria.tone()){
                sb.append("\nTONE REQUIREMENT\n");
                sb.append("Flag tweet if it is not respectful and thoughtful\n");
            }

            if(criteria.excludePolitics()){
                sb.append("\nPOLITICS\n");
                sb.append("Flag tweet if it contains discussion about politics, politicians, elections, political party or political advocacy\n");
            }

            sb.append("\nIf the tweet violates any of the conditions, isFlagged in the JSON object should be set to true, if not, set it to false\n");
            sb.append("\nHere is the tweet:\n");
            sb.append("ID: ").append(tweet.id()).append("\n");
            sb.append("Tweet: ").append(tweet.fullText()).append("\n");
        return sb.toString();
    }
}
