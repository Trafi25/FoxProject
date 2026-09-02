package com.traffipart.foxapplication.domain.data;

public record TripSummary(
        String startTime,
        String endTime,
        long durationSeconds,
        String distanceKilometers,
        int pointCount) {

}