package com.olujobii.tweet_parser;

import com.olujobii.model.Tweet;
import com.olujobii.tweet_parser.impl.TweetHandlerImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TweetHandlerTest {
    private TweetHandler tweetHandler;
    private String filePath;

    @BeforeEach
    void setUp() throws IOException{
        this.tweetHandler = new TweetHandlerImpl();
        filePath = "data/tweets.js";
        Path path = Path.of("mock-tweets.js");
        Files.deleteIfExists(path);
        Files.createFile(path);
    }

    @AfterEach
    void cleanUp() throws IOException{
        Path path = Path.of("mock-tweets.js");
        Files.deleteIfExists(path);
    }

    @Test
    void testReadTweetArchive_whenFilePathIsRead_shouldNotReturnNull() throws IOException {
        assertNotNull(tweetHandler.readTweetArchive(filePath));
    }

    @Test
    void testReadTweetArchive_whenFilePathIsRead_shouldParseTweetsSuccessfully() throws IOException{
        List<Tweet> tweets = tweetHandler.readTweetArchive(filePath);

        assertFalse(tweets.isEmpty());
        assertNotNull(tweets.getFirst().fullText());
        assertNotNull(tweets.getFirst().id());
    }
}
