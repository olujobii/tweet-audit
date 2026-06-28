package com.olujobii.model;

import com.google.gson.annotations.SerializedName;

public record Tweet(
        @SerializedName("id_str")
        String id,
        String full_text
) {
}
