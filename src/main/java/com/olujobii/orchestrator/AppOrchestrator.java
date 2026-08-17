package com.olujobii.orchestrator;

import com.olujobii.ai_client.AIProvider;
import com.olujobii.model.*;
import com.olujobii.parser.CsvParser;
import com.olujobii.parser.TweetParser;
import com.olujobii.util.PromptBuilderUtil;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AppOrchestrator {
    private final TweetParser tweetParser;
    private final CsvParser csvParser;
    private final AIProvider aiProvider;
    private final String filePath;
    private final String processedTweetsPath;
    private final Criteria criteria;

    public AppOrchestrator(TweetParser tweetParser,
                           CsvParser csvParser,
                           AIProvider aiProvider,
                           String filePath,
                           String processedTweetsPath,
                           Criteria criteria) {

        this.tweetParser = tweetParser;
        this.csvParser = csvParser;
        this.aiProvider = aiProvider;
        this.filePath = filePath;
        this.processedTweetsPath = processedTweetsPath;
        this.criteria = criteria;
    }

    public void run() throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException{
        List<Tweet> tweets = tweetParser.readFile(filePath);

        if(tweets.isEmpty()){
            System.out.println("You have no archived tweets to analyze");
            return;
        }

        //Reading processed tweets from file
        Set<String> processedTweetsFromCsv = csvParser.readProcessedTweetsFile(processedTweetsPath)
                        .orElse(new HashSet<>());

        batchElements(criteria, tweets, processedTweetsFromCsv);
    }

    private void batchElements(Criteria criteria, List<Tweet> tweets, Set<String> processedTweetsFromCsv) throws
            IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException{

        final int noOfElementsInBatch = 5;
        int noOfBatchGroup = tweets.size() / noOfElementsInBatch;
        int totalBatchToProcess = tweets.size() % noOfElementsInBatch != 0 ? (noOfBatchGroup + 1) : noOfBatchGroup;

        System.out.println("Tweets will be analyzed in "+totalBatchToProcess+" batches, each batch containing 5 tweets");

        int counter = 1;
        while(counter <= noOfBatchGroup){
            int startingIndex = (counter - 1) * noOfElementsInBatch;
            int endingIndex = counter * noOfElementsInBatch;

            List<Tweet> batchedTweets = checkIfTweetIsProcessed(new ArrayList<>(tweets.subList(startingIndex,endingIndex)), processedTweetsFromCsv);

            if(batchedTweets.isEmpty()) {
                System.out.println("All tweets has been processed in Batch "+counter);
                counter++;
                continue;
            }

            System.out.println("BATCH "+counter+"/"+totalBatchToProcess);

            String prompt = PromptBuilderUtil.buildPrompt(criteria, batchedTweets);

            analyzeTweets(prompt);
            counter++;
        }

        //Checks if there is a remainder or if the list size is less than noOfElementsInBatch
        int remainderStartIndex = noOfElementsInBatch * noOfBatchGroup;
        if(remainderStartIndex < tweets.size()){
            List<Tweet> batchedTweets = checkIfTweetIsProcessed(new ArrayList<>(tweets.subList(remainderStartIndex,tweets.size())), processedTweetsFromCsv);

            if(batchedTweets.isEmpty()) {
                System.out.println("All tweets has been processed in Batch "+counter);
                return;
            }

            System.out.println("BATCH "+counter+"/"+totalBatchToProcess);
            String prompt = PromptBuilderUtil.buildPrompt(criteria, batchedTweets);

            analyzeTweets(prompt);
        }
    }

    private void analyzeTweets(String prompt)throws
            IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException{

        //Send prompt to AI and get response in ArrayList
        List<ModelResponseTweet> modelResponseTweets = new ArrayList<>(aiProvider.analyzeTweets(prompt));

        //Get tweets flagged by AI
        List<FlaggedTweet> flaggedTweets = new ArrayList<>(getFlaggedTweets(modelResponseTweets));

        //Get Tweet ID for all processed tweets
        Set<String> processedTweets = new HashSet<>(getProcessedTweets(modelResponseTweets));

        if(!flaggedTweets.isEmpty()){
            String outputPath = "flagged-tweets_"+ LocalDate.now()+".csv";
            csvParser.parseFlaggedTweetsToCSVFile(flaggedTweets, outputPath);
            flaggedTweets.clear();
        }

        csvParser.parseProcessedTweetsToCSVFile(processedTweets, processedTweetsPath);
        processedTweets.clear();
        modelResponseTweets.clear();
    }

    private List<FlaggedTweet> getFlaggedTweets(List<ModelResponseTweet> modelResponseTweets){
        return modelResponseTweets.stream()
                .filter(ModelResponseTweet::isFlagged)
                .map(tweet -> new FlaggedTweet(buildTweetURL(tweet.id()), tweet.classification()))
                .toList();
    }

    private Set<String> getProcessedTweets(List<ModelResponseTweet> modelResponseTweets){
        return modelResponseTweets.stream()
                .map(ModelResponseTweet::id)
                .collect(Collectors.toSet());
    }

    private List<Tweet> checkIfTweetIsProcessed(List<Tweet> tweets, Set<String> processedTweets){
        if(processedTweets.isEmpty())
            return tweets;

        tweets.removeIf(tweet -> processedTweets.contains(tweet.id()));

        return tweets;
    }

    private String buildTweetURL(String id){
        return "https://x.com/username/status/"+ id;
    }
}