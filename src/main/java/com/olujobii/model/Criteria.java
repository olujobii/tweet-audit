package com.olujobii.model;

import java.util.List;

public record Criteria(
        List<String> forbiddenWords,
        boolean professionalCheck,
        boolean tone,
        boolean excludePolitics
) {
}
