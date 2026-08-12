package com.olujobii.parser;

import com.olujobii.model.FlaggedTweet;
import com.olujobii.model.Reason;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CsvParserTest {
    private CsvParser csvParser;
    private String outputPath;


    @BeforeEach
    void setup(){
        this.csvParser = new CsvParser();
        this.outputPath = "src/test/resources/test-flagged-tweets_"+ LocalDate.now()+".csv";
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Path.of(outputPath));

    }

    @AfterAll
    static void cleanupProcessedTweets() throws IOException{
        Files.deleteIfExists(Path.of("src/test/resources/test-processed-tweets.csv"));
    }

    @Test
    @Order(1)
    void testParseFlaggedTweetsToCSVFile_whenFlaggedTweetsIsNotEmpty_shouldParseSuccessfully(){
        List<FlaggedTweet> flaggedTweets = new ArrayList<>(List.of(
                new FlaggedTweet("https://x.com/username/status/243343", Reason.UNPROFESSIONAL.getValue()),
                new FlaggedTweet("https://x.com/username/status/323432",Reason.FORBIDDEN_WORDS.getValue())
        ));

        assertDoesNotThrow(() -> csvParser.parseFlaggedTweetsToCSVFile(flaggedTweets, outputPath),
                "Expected not to throw any exception");
        assertEquals(2, flaggedTweets.size(),"flaggedTweets size expected to be 2");
        assertEquals("https://x.com/username/status/243343", flaggedTweets.getFirst().tweet_url());
        assertEquals(Reason.UNPROFESSIONAL.getValue(), flaggedTweets.getFirst().classification());
    }

    @Test
    @Order(2)
    void testParseProcessedTweetsToCSVFile_whenProcessedTweetsIsNotEmpty_shouldParseSuccessfully(){
        String processedTweetsOutputPath = "src/test/resources/test-processed-tweets.csv";

        Set<String> processedTweets = new HashSet<>(Set.of(
                "3354232323",
                "2434343323"
        ));

        assertDoesNotThrow(() -> csvParser.parseProcessedTweetsToCSVFile(processedTweets, processedTweetsOutputPath));
        assertEquals(2, processedTweets.size(), "processedTweets size expected to be 2");
        assertTrue(processedTweets.contains("3354232323"));
        assertTrue(processedTweets.contains("2434343323"));
    }

    @Test
    @Order(3)
    void testReadProcessedTweetsFile_whenFileExistAndNotBlank_shouldReadSuccessfully() throws IOException{
        String processedTweetsOutputPath = "src/test/resources/test-processed-tweets.csv";

        assertDoesNotThrow(() -> csvParser.readProcessedTweetsFile(processedTweetsOutputPath));
        assertEquals(2, csvParser.readProcessedTweetsFile(processedTweetsOutputPath).get().size());
    }
}
