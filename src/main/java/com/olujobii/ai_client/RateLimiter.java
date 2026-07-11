package com.olujobii.ai_client;

public class RateLimiter {
    private final int baseDelay = 1000;
    private int attempt = 0;
    private final int maxRetries = 5;

    public int getAttempt() {
        return attempt;
    }

    public void incrementAttemptCount() {
        ++attempt;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public int getWaitTime() {
        return baseDelay * (int) Math.pow(2, attempt);
    }
}
