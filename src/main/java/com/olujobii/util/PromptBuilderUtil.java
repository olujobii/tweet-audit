package com.olujobii.util;

import com.olujobii.model.Criteria;
import com.olujobii.model.Tweet;

import java.util.List;

public class PromptBuilderUtil {

    public static String buildPrompt(Criteria criteria, List<Tweet> tweet){
        StringBuilder sb = new StringBuilder();
            sb.append("MODERATION RULES\n");
            sb.append("Flag any of the tweet if any of the conditions are true:\n");

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

            sb.append("\nThe tweets will be in a batch\n");
            sb.append("\nReturn all tweets provided back as a response no matter the outcome, stating your reason for your decision. ");
            sb.append("If any tweets violates the condition, isFlagged should be true. ");
            sb.append("Otherwise, it should be false\n");
            sb.append("\nHere are the tweets:\n");
            for(int i = 0 ; i < tweet.size(); i++){
                int tweetNumber = i + 1;
                sb.append("\nTWEET ").append(tweetNumber).append("\n");
                sb.append("ID: ").append(tweet.get(i).id()).append("\n");
                sb.append("Tweet: ").append(tweet.get(i).fullText()).append("\n");

                if(i != tweet.size() - 1)
                    sb.append("------------------------------\n");
            }
        return sb.toString();
    }
}
