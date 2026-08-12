package com.olujobii.parser;

import com.google.gson.JsonSyntaxException;
import com.olujobii.model.Tweet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test for TweetParser")
public class TweetParserTest {
    private TweetParser tweetParser;

    @BeforeEach
    void setup(){
        this.tweetParser = new TweetParser();
    }

    @Test
    @DisplayName("Testing when tweet file path is read successfully")
    void testReadFile_whenFilePathIsRead_shouldReturnValidTweets() throws IOException {
        //Arrange
        String filePath = "src/test/resources/tweets.js";

        //Act
        List<Tweet> tweets = tweetParser.readFile(filePath);
        Tweet tweet = tweets.get(1);

        //Assert
        assertNotNull(tweets, "tweets should not be null");
        assertEquals(5, tweets.size(), "Actual tweets size does not match expected tweets size");
        assertEquals("1723456789012345602",tweet.id(),
                "Actual Tweet Id does not match the expected Tweet ID");
        assertEquals("@NaijaFoodiesHub abeg no vex, e sweet die\uD83D\uDD25", tweet.fullText(),
                "Actual Tweet content does not match the expected tweet content");
    }

    @Test
    @DisplayName("Testing when tweet file path does not exist")
    void testReadFile_whenFilePathDoesNotExist_shouldThrowIOException(){
        String filePath = "src/test/resources/mock-tweerts.js";

        assertThrows(IOException.class, () -> tweetParser.readFile(filePath));

    }

    @Test
    @DisplayName("Testing when tweet file path is not a valid JSON array structure")
    void testReadFile_whenFilePathIsNotAValidJsonStructure_shouldThrowJsonSyntaxException(){
        String filePath = "src/test/resources/malformed-tweets.js";

        assertThrows(JsonSyntaxException.class, () -> tweetParser.readFile(filePath));
    }
}
