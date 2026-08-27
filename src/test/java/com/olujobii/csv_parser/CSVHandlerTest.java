package com.olujobii.csv_parser;

import com.olujobii.csv_parser.impl.CSVHandlerImpl;
import com.olujobii.model.FlaggedTweet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CSVHandlerTest {
    private CSVHandler csvHandler;

    @BeforeEach
    void setUp(){
        csvHandler = new CSVHandlerImpl();
    }

    @AfterEach
    void cleanUp() throws IOException{
        Files.deleteIfExists(Path.of("data/output/mock-flags.csv"));
    }

    @Test
    void testWriteFlaggedTweetsToCsvFile_whenFlaggedTweetsIsNotEmpty_shouldWriteSuccessfully() throws IOException {
        List<FlaggedTweet> flaggedTweets = new ArrayList<>();
        flaggedTweets.add(new FlaggedTweet("https://x.com/username/status/12345", "Discrimination"));
        flaggedTweets.add(new FlaggedTweet("https://x.com/username/status/67890", "Unprofessional words"));

        String filePath = "mock-flags.csv";

        csvHandler.writeFlaggedTweetsToCSVFile(flaggedTweets, filePath);

        assertEquals("https://x.com/username/status/12345", flaggedTweets.getFirst().tweet_url());
        assertEquals("Discrimination", flaggedTweets.getFirst().classification());
    }
}
