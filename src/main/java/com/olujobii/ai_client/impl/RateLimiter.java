package com.olujobii.ai_client.impl;

class RateLimiter {
    private final Integer requestPerMinute;
    private final long interval;
    private long lastProcessedTime;

    public RateLimiter(Integer requestPerMinute){
        this.requestPerMinute = requestPerMinute;
        this.interval = calculateInterval();
    }

    private long calculateInterval(){
        return (long) ((60.0 / requestPerMinute) * 1000);
    }

    public void checkLimit() throws InterruptedException{
        //This is for first ever request
        if(lastProcessedTime == 0) {
            lastProcessedTime = System.currentTimeMillis();
            return;
        }

        long difference = System.currentTimeMillis() - lastProcessedTime;

        if(difference < interval){
            long timeToSleep = interval - difference;
            Thread.sleep(timeToSleep);
        }

        lastProcessedTime = System.currentTimeMillis();
    }
}
