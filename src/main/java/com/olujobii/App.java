package com.olujobii;


import java.io.IOException;

public class App {

    public static void main( String[] args ) throws IOException {
        TweetImporter tweetImporter = new TweetImporter();

        tweetImporter.readFile();
    }
}
