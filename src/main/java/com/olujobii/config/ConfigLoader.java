package com.olujobii.config;

import com.google.gson.Gson;
import com.olujobii.model.Criteria;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class ConfigLoader {

    public static Criteria readConfigFile(String config) throws IOException {
        Gson gson = new Gson();

        try(BufferedReader reader = Files.newBufferedReader(Path.of(config))){
            String criteriaContent = reader.lines().collect(Collectors.joining());

            if(criteriaContent.isBlank())
                throw new NullPointerException("Empty file for path: "+config+". Criteria cannot be null");


            return gson.fromJson(criteriaContent, Criteria.class);
        }
    }
}
