package com.olujobii.parser;

import com.google.gson.Gson;
import com.olujobii.model.Criteria;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CriteriaParser {

    public Criteria readConfigFile(Gson gson, String config) throws IOException {

        try(BufferedReader reader = Files.newBufferedReader(Path.of(config))){

            return gson.fromJson(reader, Criteria.class);
        }
    }
}
