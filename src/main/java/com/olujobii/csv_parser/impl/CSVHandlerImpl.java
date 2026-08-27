package com.olujobii.csv_parser.impl;

import com.olujobii.csv_parser.CSVHandler;
import com.olujobii.model.FlaggedTweet;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class CSVHandlerImpl implements CSVHandler {

    @Override
    public void writeFlaggedTweetsToCSVFile(List<FlaggedTweet> flaggedTweets, String outputPath) throws IOException {
        Path path = Path.of("data/output/"+outputPath);

        boolean isFileDoesNotExistsOrEmpty = Files.notExists(path) || isFileEmpty(path);

        HeaderColumnNameMappingStrategy<FlaggedTweet> strategy = new HeaderColumnNameMappingStrategyBuilder<FlaggedTweet>().build();
        strategy.setType(FlaggedTweet.class);
        strategy.setColumnOrderOnWrite((s1, s2) -> {
            int compare = s1.compareToIgnoreCase(s2);
            return Integer.compare(0, compare);
        });

        //If true, it should create new file and insert headers. If false, it should append to file and skip insertion of headers.
        if(isFileDoesNotExistsOrEmpty) {
            createDirectoryIfNotExists();
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {

                StatefulBeanToCsv<FlaggedTweet> beanToCsv = new StatefulBeanToCsvBuilder<FlaggedTweet>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(flaggedTweets);
            }catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex){
                throw new RuntimeException("Error occurred while writing flagged tweets to "+outputPath, ex);
            }
        }else{
            try(BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND);
                StringWriter stringWriter = new StringWriter()){

                StatefulBeanToCsv<FlaggedTweet> beanToCsv = new StatefulBeanToCsvBuilder<FlaggedTweet>(stringWriter)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(flaggedTweets);

                String contents = stringWriter.toString();
                int index = contents.indexOf("\n") + 1;
                String dataOnly = contents.substring(index);
                writer.write(dataOnly);
            }catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException ex){
                throw new RuntimeException("Error occurred while writing flagged tweets to "+outputPath, ex);
            }
        }
    }

    private boolean isFileEmpty(Path path) throws IOException {
        try(BufferedReader reader = Files.newBufferedReader(path)){
            return reader.lines().collect(Collectors.joining()).isBlank();
        }
    }

    private void createDirectoryIfNotExists() throws IOException{
        Path path = Path.of("data/output/");
        if(Files.notExists(path))
            Files.createDirectory(path);
    }
}
