package com.traffipart.foxapplication.domain.data;

public record TripLoadResult(
        TripSummary summary,
        boolean fromCache
) {
}
