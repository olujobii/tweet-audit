package com.olujobii.ai_client.impl;

class RateLimiter {
    private final int requestPerMinute;
    private final long interval;
    private long lastProcessedTime;

    public RateLimiter(int requestPerMinute){
        this.requestPerMinute = requestPerMinute;
        this.interval = calculateInterval();
    }

    private long calculateInterval(){
        return (long) ((60.0 / requestPerMinute) * 1000);
    }

    public void checkLimit() throws InterruptedException{
        //This is for first ever request
        if(lastProcessedTime == 0) {
            System.out.println("First ever tweets, good to go in Rate Limiter");
            lastProcessedTime = System.currentTimeMillis();
            return;
        }

        long difference = System.currentTimeMillis() - lastProcessedTime;

        if(difference < interval){
            long timeToSleep = interval - difference;
            System.out.println("Still lower than "+ interval +"ms. Will sleep now for "+timeToSleep+"ms in rate limiter");
            Thread.sleep(timeToSleep);
        }else{
            System.out.println("Good to go in rate limiter. New time set");
        }

        lastProcessedTime = System.currentTimeMillis();
    }
}
