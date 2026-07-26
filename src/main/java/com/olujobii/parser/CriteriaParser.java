package com.olujobii.parser;

import com.google.gson.Gson;
import com.olujobii.model.Criteria;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CriteriaParser {
    private final Gson gson;

    public CriteriaParser(Gson gson) {
        this.gson = gson;
    }

    public Criteria readConfigFile(String config) throws IOException {

        try(BufferedReader reader = Files.newBufferedReader(Path.of(config))){

            return gson.fromJson(reader, Criteria.class);
        }
    }
}
