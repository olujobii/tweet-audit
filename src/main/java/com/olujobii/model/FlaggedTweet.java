package com.olujobii.model;

import com.opencsv.bean.CsvBindByName;

public record FlaggedTweet(
        @CsvBindByName
        String tweet_url,

        @CsvBindByName
        String classification,

        @CsvBindByName
        boolean deleted
) {
}
