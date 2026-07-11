package com.olujobii;


import com.olujobii.ai_client.GeminiClient;
import com.olujobii.orchestrator.AppOrchestrator;
import com.olujobii.parser.CriteriaParser;
import com.olujobii.parser.TweetParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    private final static String ENVIRONMENT_VARIABLE_NAME = "GOOGLE_API_KEY";

    public static void main( String[] args ) {

        if(args.length != 2){
            System.out.println("WRONG COMMAND INPUT");
            System.out.println("Usage: java -jar target/tweet-audit-1.0-SNAPSHOT.JAR <archive-path> <config-path>");
            System.out.println("archive-path: The path to the file containing your tweet archives");
            System.out.println("criteria-path: The path to the file containing your alignment criteria");
            return;
        }

        String archivePath = args[0];
        String criteriaPath = args[1];

        if(Files.isDirectory(Path.of(archivePath))){
            System.out.println("The archive path specified must be a file, not a directory");
            return;
        }

        if(Files.isDirectory(Path.of(criteriaPath))){
            System.out.println("The criteria path specified must be a file, not a directory");
            return;
        }

        if(System.getenv(ENVIRONMENT_VARIABLE_NAME) == null || System.getenv(ENVIRONMENT_VARIABLE_NAME).isBlank()){
            System.out.println("Your API Key cannot be found in your environment variables. Kindly go through the README for a step-by-step guide or use this link: (insert google link)");
            return;
        }

        TweetParser tweetParser = new TweetParser();
        CriteriaParser criteriaParser = new CriteriaParser();
        GeminiClient geminiClient = new GeminiClient();
        AppOrchestrator appOrchestrator = new AppOrchestrator(tweetParser, criteriaParser, geminiClient, archivePath, criteriaPath);

        try{
            appOrchestrator.run();
        }catch(IOException ex){
            System.out.println("Error Occurred: "+ex.getMessage());
        }catch(InterruptedException ex){
            System.out.println("Error occurred: "+ex.getMessage());
        }
    }
}
