package com.traffipart.foxapplication.ui;

import com.traffipart.foxapplication.domain.data.TripSummary;

public record TripUiState(
        Status status,
        TripSummary summary,
        boolean fromCache,
        String errorMessage
) {

    public enum Status {
        LOADING,
        CONTENT,
        ERROR
    }

    public static TripUiState loading() {
        return new TripUiState(
                Status.LOADING,
                null,
                false,
                null
        );
    }

    public static TripUiState content(
            TripSummary summary,
            boolean fromCache
    ) {
        return new TripUiState(
                Status.CONTENT,
                summary,
                fromCache,
                null
        );
    }

    public static TripUiState error(String message) {
        return new TripUiState(
                Status.ERROR,
                null,
                false,
                message
        );
    }
}