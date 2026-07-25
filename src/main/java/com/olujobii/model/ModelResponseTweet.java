package com.olujobii.model;

public final class ModelResponseTweet {
    private final String id;
    private final String tweet;
    private final String classification;
    private final boolean isFlagged;
    private boolean isFlaggedTweetSaved;

    public ModelResponseTweet(
            String id,
            String tweet,
            String classification,
            boolean isFlagged,
            boolean isFlaggedTweetSaved
    ) {
        this.id = id;
        this.tweet = tweet;
        this.classification = classification;
        this.isFlagged = isFlagged;
        this.isFlaggedTweetSaved = isFlaggedTweetSaved;
    }

    public String getId() {
        return id;
    }

    public String getTweet() {
        return tweet;
    }

    public String getClassification() {
        return classification;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public boolean isFlaggedTweetSaved() {
        return isFlaggedTweetSaved;
    }

    public void setIsFlaggedTweetSaved(){
        this.isFlaggedTweetSaved = true;
    }

    @Override
    public String toString() {
        return "ModelResponseTweet[" +
                "id=" + id + ", " +
                "tweet=" + tweet + ", " +
                "classification=" + classification + ", " +
                "isFlagged=" + isFlagged + ", " +
                "isFlaggedTweetSaved=" + isFlaggedTweetSaved + ']';
    }

}
