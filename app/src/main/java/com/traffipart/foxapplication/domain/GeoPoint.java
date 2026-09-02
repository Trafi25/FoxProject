package com.traffipart.foxapplication.domain;

public record GeoPoint(
        String time,
        long timestampMillis,
        double latitude,
        double longitude) {

}