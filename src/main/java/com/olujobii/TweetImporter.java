package com.olujobii;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.olujobii.model.TweetWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class TweetImporter {
    private static final String PATH = "tweets.js";
    private final Gson gson;

    public TweetImporter(){
        gson = new Gson();
    }

    public void readFile() throws IOException{
        Path path = Path.of(PATH);
        List<TweetWrapper> tweets;

        try(BufferedReader reader = Files.newBufferedReader(path)){
            Type type = new TypeToken<List<TweetWrapper>>(){}.getType();

            //This ensures Gson starts parsing from the beginning of array "[" to match a valid JSON structure.
            String read = reader.lines().collect(Collectors.joining());
            int startContent = read.indexOf("[");
            tweets = gson.fromJson(read.substring(startContent), type);
        }
        tweets.forEach(System.out::println);
    }
}