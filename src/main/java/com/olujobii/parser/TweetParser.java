package com.olujobii.parser;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.olujobii.model.Tweet;
import com.olujobii.model.TweetWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class TweetParser {

    public List<Tweet> readFile(Gson gson, String path) throws IOException{
        List<TweetWrapper> tweetWrappers;
        List<Tweet> tweets;

        try(BufferedReader reader = Files.newBufferedReader(Path.of(path))){
            Type type = new TypeToken<List<TweetWrapper>>(){}.getType();

            //This ensures Gson starts parsing from the beginning of array "[" to match a valid JSON structure.
            String read = reader.lines().collect(Collectors.joining());
            int startContent = read.indexOf("[");
            tweetWrappers = gson.fromJson(read.substring(startContent), type);
        }

        tweets = tweetWrappers.stream().map(tweet -> new Tweet(tweet.tweet().id(), tweet.tweet().full_text())).toList();

        return tweets;
    }

}