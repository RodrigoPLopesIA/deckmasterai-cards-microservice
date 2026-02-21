package com.deckmasterai.cards.dto;

import java.util.Map;

public record ResponseErrorDTO(String path, String message, int statusCode, Map<String, String> details) {

    public ResponseErrorDTO(String path, String message, int statusCode) {
        this(path, message, statusCode, null);
    }
}