package com.olujobii.tweet_parser.impl;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.olujobii.model.Tweet;
import com.olujobii.model.TweetWrapper;
import com.olujobii.tweet_parser.TweetHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TweetHandlerImpl implements TweetHandler {
    private static final String PROCESSED_TWEETS_FILE_PATH = "data/processed-tweets.txt";

    @Override
    public List<Tweet> readTweetArchive(String path) throws IOException{
        Gson gson = new Gson();
        List<TweetWrapper> tweetWrappers;

        try(BufferedReader reader = Files.newBufferedReader(Path.of(path))){
            Type type = new TypeToken<List<TweetWrapper>>(){}.getType();

            //This ensures Gson starts parsing from the beginning of array "[" to match a valid JSON array structure.
            String read = reader.lines().collect(Collectors.joining());
            int startContent = read.indexOf("[");
            tweetWrappers = gson.fromJson(read.substring(startContent), type);
        }

        return tweetWrappers.stream().map(tweet -> new Tweet(tweet.tweet().id(), tweet.tweet().fullText())).toList();
    }

    @Override
    public void writeProcessedTweets(Set<String> processedTweets) throws IOException {
        try(BufferedWriter writer = Files.newBufferedWriter(Path.of(PROCESSED_TWEETS_FILE_PATH), StandardOpenOption.CREATE, StandardOpenOption.APPEND)){
            for(String tweet : processedTweets){
                writer.write(tweet +"\n");
            }
        }
    }

    @Override
    public Set<String> readProcessedTweets() throws IOException {
        Path path = Path.of(PROCESSED_TWEETS_FILE_PATH);
        if(Files.exists(path) && !checkIfProcessedTweetFileIsNotBlank()){
            Set<String> processedTweets = new HashSet<>();
            try(BufferedReader reader = Files.newBufferedReader(path)){
                reader.lines().forEach(processedTweets::add);
            }
            return processedTweets;
        }
        return null;
    }

    private boolean checkIfProcessedTweetFileIsNotBlank() throws IOException{
        try(BufferedReader reader = Files.newBufferedReader(Path.of(PROCESSED_TWEETS_FILE_PATH))){
            return reader.lines().collect(Collectors.joining()).isBlank();
        }
    }
}