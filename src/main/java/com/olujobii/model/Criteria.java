package com.olujobii.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record Criteria(
        @SerializedName("forbidden_words")
        List<String> forbiddenWords,

        @SerializedName("professional_check")
        boolean professionalCheck,

        boolean tone,

        @SerializedName("exclude_politics")
        boolean excludePolitics
) {
}
