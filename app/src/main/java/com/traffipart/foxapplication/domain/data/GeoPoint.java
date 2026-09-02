package com.traffipart.foxapplication.domain.data;

public record GeoPoint(
        String time,
        long timestampMillis,
        double latitude,
        double longitude) {

}