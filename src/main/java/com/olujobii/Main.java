package com.olujobii;

import com.olujobii.ai_client.AIProvider;
import com.olujobii.ai_client.impl.GeminiClientImpl;
import com.olujobii.config.ConfigLoader;
import com.olujobii.csv_parser.CSVHandler;
import com.olujobii.model.Criteria;
import com.olujobii.orchestrator.AppOrchestrator;
import com.olujobii.csv_parser.impl.CSVHandlerImpl;
import com.olujobii.tweet_parser.TweetHandler;
import com.olujobii.tweet_parser.impl.TweetHandlerImpl;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main( String[] args ) {

        if(args.length < 3){
            System.out.println("WRONG COMMAND INPUT");
            System.out.println("Usage: java -jar target/tweet-audit-1.0-SNAPSHOT.JAR <archive-path.js> <config-path.json> [--rpm]");
            System.out.println("archive-path: The path to the file containing your tweet archives");
            System.out.println("criteria-path: The path to the file containing your alignment criteria");
            System.out.println("output-path: desired name of file to persist the tweets marked for deletion");
            System.out.println("--rpm: Customizable configuration to specify request per minute if user is not in Free tier.");
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down.......\nAny processed tweet has been saved and you can resume later");
            System.out.println(Paths.get("").toAbsolutePath());

        }));

        int argsSize = args.length;
        String archivePath = args[0];
        String configPath = args[1];
        String outputPath = args[2];
        int requestPerMinute;

        try {
            requestPerMinute = getUserDefinedRequestPerMinute(argsSize, args);
        } catch (NumberFormatException __) {
            System.out.println("RPM specified is not a valid input: "+args[3]);
            return;
        }

        checkFileExtension(archivePath,"js");
        checkFileExtension(configPath, "json");
        checkFileExtension(outputPath, "csv");

        String env = System.getenv("GOOGLE_API_KEY");
        if(env == null || env.isBlank()){
            System.out.println("Your API Key cannot be found in your environment variables (GOOGLE_API_KEY)");
            System.exit(1);
        }


        try{
            AppOrchestrator appOrchestrator = getAppOrchestrator("data/"+archivePath, getUserCriteria("data/"+configPath), outputPath, requestPerMinute);
            System.out.println("=====TWEET AUDITING CLI TOOL=====");
            appOrchestrator.run();
        }catch(IOException ex){
            System.out.println("Failed to process file: "+ex.getMessage());
        } catch(Exception ex){
            System.out.println("Error Occurred: "+ex.getMessage());
        }
    }

    private static int getUserDefinedRequestPerMinute(int argsSize, String[] args)throws NumberFormatException{
        if(argsSize != 4)
            return 15;// Default RPM for Gemini 3.1 flash lite model(Free tier)

        if(args[2] != null && !args[2].equals("--rpm")){
            System.out.println(args[2]+" is not a valid configurable. Kindly refer to README.md for guidance");
            System.exit(1);
        }

        int requestPerMinute = Integer.parseInt(args[3]);
        if(requestPerMinute < 15){
            System.out.println("RPM specified is less than 15(which is lower than RPM specified for free tier of this model)." +
                    "Kindly specify a valid RPM");
            System.exit(1);
        }

        return requestPerMinute;
    }

    private static AppOrchestrator getAppOrchestrator(String archivePath, Criteria criteria, String outputPath, int requestPerMinute) {
        TweetHandler tweetHandler = new TweetHandlerImpl();
        CSVHandler csvHandler = new CSVHandlerImpl();
        AIProvider aiProvider = new GeminiClientImpl(requestPerMinute);

        return new AppOrchestrator(tweetHandler, csvHandler, aiProvider,
                archivePath, criteria, outputPath);
    }

    private static void checkFileExtension(String args, String expectedFileExtension){
        String[] arr = args.split("\\.");

        if(!arr[arr.length - 1].contains(expectedFileExtension)){
            System.out.println("The file path you specified for "+arr[0]+" does not match the expected file extension: ."+expectedFileExtension);
            System.out.println("Make sure your file path are specified with the expected file extension");
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
