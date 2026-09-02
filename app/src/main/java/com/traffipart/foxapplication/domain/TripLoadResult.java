package com.traffipart.foxapplication.domain;

public record TripLoadResult(
        TripSummary summary,
        boolean fromCache
) {
}
