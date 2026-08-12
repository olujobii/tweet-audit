package com.olujobii.parser;

import com.olujobii.model.FlaggedTweet;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
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

public class CsvParser {

    public void parseFlaggedTweetsToCSVFile(List<FlaggedTweet> flaggedTweets, String outputPath) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        Path path = Path.of(outputPath);

        boolean isFileDoesNotExistsOrEmpty = Files.notExists(path) || isFileEmpty(path);

        HeaderColumnNameMappingStrategy<FlaggedTweet> strategy = new HeaderColumnNameMappingStrategyBuilder<FlaggedTweet>().build();
        strategy.setType(FlaggedTweet.class);
        strategy.setColumnOrderOnWrite((s1, s2) -> {
            int compare = s1.compareToIgnoreCase(s2);
            return Integer.compare(0, compare);
        });

        //If true, it should create new file and insert headers. If false, it should append to file and skip insertion of headers.
        if(isFileDoesNotExistsOrEmpty) {
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {

                StatefulBeanToCsv<FlaggedTweet> beanToCsv = new StatefulBeanToCsvBuilder<FlaggedTweet>(writer)
                        .withMappingStrategy(strategy)
                        .build();

                beanToCsv.write(flaggedTweets);
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
            }
        }
    }

    public void parseProcessedTweetsToCSVFile(Set<String> processedTweets, String processedTweetsPath) throws IOException {

        try(BufferedWriter writer =  Files.newBufferedWriter(Path.of(processedTweetsPath), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
            ICSVWriter csvWriter = new CSVWriterBuilder(writer)
                    .withSeparator('\n')
                    .build()){
            String[] tweets = processedTweets.toArray(new String[0]);
            List<String[]> sent = new ArrayList<>();

            sent.add(tweets);
            csvWriter.writeAll(sent, false);
        }
    }

    public Optional<Set<String>> readProcessedTweetsFile(String processedTweetsPath) throws IOException{
        Path path = Path.of(processedTweetsPath);
        boolean fileExists = Files.exists(path) && !isFileEmpty(path);

        if(fileExists){
            try(BufferedReader reader = Files.newBufferedReader(path)){

                String[] processedTweets = reader.lines().toArray(String[]::new);

                return Optional.of(Set.copyOf(Arrays.stream(processedTweets).collect(Collectors.toSet())));
            }
        }
        return Optional.empty();
    }

    private boolean isFileEmpty(Path path) throws IOException {
        try(BufferedReader reader = Files.newBufferedReader(path)){
            return reader.lines().collect(Collectors.joining()).isBlank();
        }
    }
}
