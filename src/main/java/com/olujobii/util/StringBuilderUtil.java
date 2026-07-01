package com.olujobii.util;

import com.olujobii.model.Criteria;
import com.olujobii.model.Tweet;

import java.util.List;

public class StringBuilderUtil {

    public static String buildAlignmentCriteriaString(Criteria criteria){
        StringBuilder criteriaString = new StringBuilder();

        if(!criteria.forbiddenWords().isEmpty()){
            criteriaString.append("\n");
            criteriaString.append("FORBIDDEN WORDS\n");
            criteriaString.append("Flag tweets that contain any of these words: \n");
            criteria.forbiddenWords().forEach(word -> criteriaString.append(word).append("\n"));
        }

        if(criteria.professionalCheck()){
            criteriaString.append("\n");
            criteriaString.append("PROFESSIONALISM CHECK\n");
            criteriaString.append("Flag tweets that appear unprofessional\n");
            criteriaString.append("\n");
            criteriaString.append("Examples:\n");
            criteriaString.append("Insulting\n").append("Offensive remarks\n").append("Racial abuse\n");
        }

        if(criteria.tone()){
            criteriaString.append("\n");
            criteriaString.append("TONE OF TWEET\n");
            criteriaString.append("Flag tweets that do not sound respectful and thoughtful e.g. Mockery\n");
        }

        if(criteria.excludePolitics()){
            criteriaString.append("\n");
            criteriaString.append("POLITICAL CONTRIBUTION\n");
            criteriaString.append("Flag tweets discussing politics, political party or political advocacy\n");
        }

        return criteriaString.toString();
    }

    //FIXME: Loop is static right now for batching, make it dynamic
    public static String buildTweetsString(List<Tweet> tweets){
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < 15 ; i++){
            int index = i + 1;
            sb.append("[TWEET ").append(index).append("]\n");
            sb.append("ID: ").append(tweets.get(i).id()).append("\n");
            sb.append("Tweet: ").append(tweets.get(i).full_text()).append("\n");
            sb.append("----------------------------\n");
        }

        return sb.toString();
    }
}
