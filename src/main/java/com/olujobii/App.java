package com.olujobii;


import java.io.IOException;

public class App {

    public static void main( String[] args ) {

        if(args.length != 1){
            System.out.println("WRONG COMMAND INPUT");
            System.out.println("Usage: java -jar target/tweet-audit-1.0-SNAPSHOT.JAR <archive-path>");
            return;
        }

        TweetImporter tweetImporter = new TweetImporter();

        String path = args[0];
//        String config = args[1];
        try{
            tweetImporter.readFile(path);
        }catch(IOException ex){
            System.out.println("Error Occurred, file not found: "+ex.getMessage());
            ex.printStackTrace();
        }
    }
}
