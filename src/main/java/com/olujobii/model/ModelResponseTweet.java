package com.olujobii.model;

import org.jspecify.annotations.NonNull;

public record ModelResponseTweet(String id, String tweet, String classification, boolean isFlagged) {

    @Override
    public @NonNull String toString() {
        return "ModelResponseTweet[" +
                "id=" + id + ", " +
                "tweet=" + tweet + ", " +
                "classification=" + classification + ", " +
                "isFlagged=" + isFlagged + ']';
    }

}
