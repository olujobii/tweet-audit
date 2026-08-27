package com.olujobii.tweet_parser;

import com.olujobii.model.Tweet;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface TweetHandler {
    List<Tweet> readTweetArchive(String path) throws IOException;

    void writeProcessedTweets(Set<String> processedTweets) throws IOException;

    Set<String> readProcessedTweets() throws IOException;
}
