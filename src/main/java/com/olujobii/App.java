package com.olujobii;


import java.io.IOException;

public class App {

    public static void main( String[] args ) {

        if(args.length != 2){
            System.out.println("WRONG COMMAND INPUT");
            System.out.println("Usage: java -jar target/tweet-audit-1.0-SNAPSHOT.JAR <archive-path> <config-path>");
            return;
        }

        String path = args[0];
        String config = args[1];

        TweetHandler tweetHandler = new TweetHandler();
        CriteriaHandler criteriaHandler = new CriteriaHandler();
        GeminiClient geminiClient = new GeminiClient();
        Orchestrator orchestrator = new Orchestrator(tweetHandler, criteriaHandler, geminiClient, path, config);

        try{
            orchestrator.run();
        }catch(IOException ex){
            System.out.println("Error Occurred: "+ex.getMessage());
//            ex.printStackTrace();
        }
    }
}
