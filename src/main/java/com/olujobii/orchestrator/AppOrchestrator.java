package com.olujobii.orchestrator;

import com.olujobii.ai_client.AIProvider;
import com.olujobii.csv_parser.CSVHandler;
import com.olujobii.model.*;
import com.olujobii.tweet_parser.TweetHandler;
import com.olujobii.util.PromptBuilderUtil;

import java.io.IOException;
import java.util.*;

public class AppOrchestrator {
    private final TweetHandler tweetHandler;
    private final CSVHandler csvHandler;
    private final AIProvider aiProvider;
    private final String filePath;
    private final Criteria criteria;
    private final String outputPath;

    public AppOrchestrator(TweetHandler tweetHandler,
                           CSVHandler csvHandler,
                           AIProvider aiProvider,
                           String filePath,
                           Criteria criteria,
                           String outputPath) {

        this.tweetHandler = tweetHandler;
        this.csvHandler = csvHandler;
        this.aiProvider = aiProvider;
        this.filePath = filePath;
        this.criteria = criteria;
        this.outputPath = outputPath;
    }

    public void run() throws IOException {
        List<Tweet> tweets = tweetHandler.readTweetArchive(filePath);

        if(tweets.isEmpty()){
            System.out.println("You have no archived tweets to analyze");
            return;
        }

        //Reading processed tweets from file
        Set<String> processedTweets = tweetHandler.readProcessedTweets();

        batchElements(criteria, tweets, processedTweets);
    }

    private void batchElements(Criteria criteria, List<Tweet> tweets, Set<String> processedTweetsFromCsv) throws
            IOException {

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
            IOException {

        //Send prompt to AI and get response in ArrayList
        List<ModelResponseTweet> modelResponseTweets = aiProvider.analyzeTweets(prompt);

        List<FlaggedTweet> flaggedTweets = new ArrayList<>();
        Set<String> processedTweets = new HashSet<>();

        extractFlaggedTweetAndProcessedTweetId(modelResponseTweets, flaggedTweets, processedTweets);

        if(!flaggedTweets.isEmpty()){
            csvHandler.parseFlaggedTweetsToCSVFile(flaggedTweets, outputPath);
        }

        tweetHandler.writeProcessedTweets(processedTweets);
    }

    private void extractFlaggedTweetAndProcessedTweetId(List<ModelResponseTweet> modelResponseTweets,
                                                        List<FlaggedTweet> flaggedTweets,
                                                        Set<String> processedTweets) {

        for(ModelResponseTweet tweet : modelResponseTweets){
            processedTweets.add(tweet.id());

            if(tweet.isFlagged())
                flaggedTweets.add(new FlaggedTweet(buildTweetURL(tweet.id()), tweet.classification()));
        }
    }

    private List<Tweet> checkIfTweetIsProcessed(List<Tweet> tweets, Set<String> processedTweets){
        if(processedTweets == null)
            return tweets;

        tweets.removeIf(tweet -> processedTweets.contains(tweet.id()));

        return tweets;
    }

    private String buildTweetURL(String id){
        return "https://x.com/username/status/"+ id;
    }
}