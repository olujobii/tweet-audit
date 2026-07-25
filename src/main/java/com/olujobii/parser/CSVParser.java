package com.olujobii.parser;

import com.olujobii.model.FlaggedTweet;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CSVParser {

    public void parseToCSV(List<FlaggedTweet> flaggedTweets) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        LocalDate now = LocalDate.now();
        String filePath = "flagged-tweets_"+now+".csv";
        Path path = Path.of(filePath);

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

    private boolean isFileEmpty(Path path) throws IOException {
        try(BufferedReader reader = Files.newBufferedReader(path)){
            return reader.lines().collect(Collectors.joining()).isBlank();
        }
    }
}
