package com.olujobii.orchestrator;

import com.google.gson.Gson;
import com.olujobii.ai_client.AIProvider;
import com.olujobii.model.*;
import com.olujobii.parser.CSVParser;
import com.olujobii.parser.CriteriaParser;
import com.olujobii.parser.TweetParser;
import com.olujobii.util.PromptBuilderUtil;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppOrchestrator {
    private final Gson gson;
    private final TweetParser tweetParser;
    private final CriteriaParser criteriaParser;
    private final CSVParser csvParser;
    private final AIProvider aiProvider;
    private final String filePath;
    private final String configPath;

    public AppOrchestrator(TweetParser tweetParser, CriteriaParser criteriaParser, CSVParser csvParser, AIProvider aiProvider,
                           String filePath, String configPath) {
        this.gson = new Gson();
        this.tweetParser = tweetParser;
        this.criteriaParser = criteriaParser;
        this.csvParser = csvParser;
        this.aiProvider = aiProvider;
        this.filePath = filePath;
        this.configPath = configPath;
    }


    public void run() throws IOException, InterruptedException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException{
        List<Tweet> tweets = tweetParser.readFile(gson, filePath);

        if(tweets.isEmpty()){
            System.out.println("You have no archived tweets to analyze");
            return;
        }

        System.out.println("You have "+tweets.size()+" tweets to be analyzed");

        Criteria criteria = criteriaParser.readConfigFile(gson, configPath);

        if(criteria == null || (criteria.forbiddenWords() == null && !criteria.professionalCheck() && !criteria.tone() && !criteria.excludePolitics()))
            throw new RuntimeException("Criteria alignment cannot be empty");

        batchElements(criteria, tweets);
    }

    private void batchElements(Criteria criteria, List<Tweet> tweets) throws InterruptedException,
            IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException{
        final int noOfElementsInBatch = 5;
        List<ModelResponseTweet> modelResponseTweets = new ArrayList<>();
        List<FlaggedTweet> flaggedTweets = new ArrayList<>();
        int counter = 1;
        int noOfBatchGroup = tweets.size() / noOfElementsInBatch;

        while(counter <= noOfBatchGroup){
            int startingIndex = (counter - 1) * noOfElementsInBatch;
            int endingIndex = counter * noOfElementsInBatch;

            System.out.println("BATCH "+counter);
            String prompt = PromptBuilderUtil.buildPrompt(criteria, tweets.subList(startingIndex, endingIndex));

            modelResponseTweets.addAll(aiProvider.analyzeTweets(prompt));

            //separate flagged tweets
            flaggedTweets.addAll(getFlaggedTweets(modelResponseTweets));

            if(!flaggedTweets.isEmpty()){
            //FIXME: Keeps writing header names to csv file upon each write. We need to fix that
                csvParser.parseToCSV(flaggedTweets);
                flaggedTweets.clear();
            }
            Thread.sleep(10000);
            counter++;
        }

        //Checks if there is a remainder or if the list size is less than noOfElementsInBatch
        int remainderStartIndex = noOfElementsInBatch * noOfBatchGroup;
        if(remainderStartIndex < tweets.size()){
            System.out.println("BATCH "+counter);
            String prompt = PromptBuilderUtil.buildPrompt(criteria, tweets.subList(remainderStartIndex, tweets.size()));

            modelResponseTweets.addAll(aiProvider.analyzeTweets(prompt));
            //separate flagged tweets
            flaggedTweets.addAll(getFlaggedTweets(modelResponseTweets));

            if(!flaggedTweets.isEmpty()){
                csvParser.parseToCSV(flaggedTweets);
                flaggedTweets.clear();
            }

            Thread.sleep(10000);
        }
    }

    private List<FlaggedTweet> getFlaggedTweets(List<ModelResponseTweet> modelResponseTweets){
        return modelResponseTweets.stream()
                //isFlaggedTweetSaved is necessary because it prevents duplicate writing of saved flagged tweet to csv file
                .filter(tweet -> {
                    if(tweet.isFlagged() && !tweet.isFlaggedTweetSaved()){
                        tweet.setIsFlaggedTweetSaved();
                        return true;
                    }
                    return false;
                })
                .map(tweet -> new FlaggedTweet(buildTweetURL(tweet.getId()), tweet.getClassification(), false))
                .toList();
    }

    private String buildTweetURL(String id){
        return "https://x.com/username/status/"+ id;
    }
}