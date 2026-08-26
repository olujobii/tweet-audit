package com.olujobii.csv_parser;

import com.olujobii.model.FlaggedTweet;

import java.io.IOException;
import java.util.List;

public interface CSVHandler {
    void parseFlaggedTweetsToCSVFile(List<FlaggedTweet> flaggedTweets, String outputPath) throws IOException;
}
