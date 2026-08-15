package com.olujobii;

import com.olujobii.ai_client.AIProvider;
import com.olujobii.ai_client.RetryHandler;
import com.olujobii.ai_client.impl.GeminiClientImpl;
import com.olujobii.config.ConfigLoader;
import com.olujobii.model.Criteria;
import com.olujobii.orchestrator.AppOrchestrator;
import com.olujobii.parser.CsvParser;
import com.olujobii.parser.TweetParser;

import java.io.IOException;

public class Main {

    public static void main( String[] args ) throws IOException {

        if(args.length != 3){
            System.out.println("WRONG COMMAND INPUT");
            System.out.println("Usage: java -jar target/tweet-audit-1.0-SNAPSHOT.JAR <archive-path.js> <config-path.json> <output-processed-tweets.csv>");
            System.out.println("archive-path: The path to the file containing your tweet archives");
            System.out.println("criteria-path: The path to the file containing your alignment criteria");
            System.out.println("output-processed-tweets: Indicates where your processed tweets are persisted and where you want them to be saved. " +
                    "This is crucial for resumable workflow.");
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down.......");
            System.out.println("Any processed tweet has been saved and you can resume later");
        }));

        String archivePath = args[0];
        String configPath = args[1];
        String processedTweetsPath = args[2];

        checkFileExtension(configPath, "json");
        checkFileExtension(processedTweetsPath, "csv");

        String env = System.getenv("GOOGLE_API_KEY");
        if(env == null || env.isBlank()){
            System.out.println("Your API Key cannot be found in your environment variables (GOOGLE_API_KEY)");
            return;
        }

        AppOrchestrator appOrchestrator = getAppOrchestrator(archivePath, processedTweetsPath, getUserCriteria(configPath));

        try{
            System.out.println("=====TWEET AUDITING CLI TOOL=====");
            appOrchestrator.run();
        }catch(IOException ex){
            ex.printStackTrace();
        } catch(Exception ex){
            System.out.println("Error Occurred: "+ex.getMessage());
        }
    }

    private static AppOrchestrator getAppOrchestrator(String archivePath, String processedTweetsPath, Criteria criteria) {
        TweetParser tweetParser = new TweetParser();
        CsvParser csvParser = new CsvParser();
        AIProvider aiProvider = new GeminiClientImpl();
        RetryHandler retryHandler = new RetryHandler(aiProvider);
        
        return new AppOrchestrator(tweetParser, csvParser, retryHandler,
                archivePath, processedTweetsPath, criteria);
    }

    private static void checkFileExtension(String args, String expectedFileExtension){
        String[] arr = args.split("\\.");

        if(!arr[arr.length - 1].contains(expectedFileExtension)){
            System.out.println("The file path you specified for "+arr[0]+" does not match the expected file extension: ."+expectedFileExtension);
            System.out.println("Make sure your specified path are specified with the expected file extension");
            System.exit(1);
        }
    }

    private static Criteria getUserCriteria(String configPath) throws IOException{
        Criteria criteria = ConfigLoader.readConfigFile(configPath);

        if(criteria.forbiddenWords() == null && !criteria.professionalCheck() && !criteria.tone() && !criteria.excludePolitics()){
            System.out.println("All criteria fields cannot be empty");
            System.exit(1);
        }

        return criteria;
    }
}
