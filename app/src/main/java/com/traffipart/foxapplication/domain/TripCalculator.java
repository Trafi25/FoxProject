package com.traffipart.foxapplication.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class TripCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0088;

    private TripCalculator() {
    }

    public static TripSummary summarize(List<GeoPoint> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one location point is required"
            );
        }

        GeoPoint first = points.get(0);
        GeoPoint last = points.get(points.size() - 1);

        long durationSeconds =
                (last.timestampMillis() - first.timestampMillis()) / 1000L;

        if (durationSeconds < 0) {
            throw new IllegalArgumentException(
                    "Location points sorted not chronologically"
            );
        }

        double totalKilometers = 0.0;

        for (int index = 1; index < points.size(); index++) {
            GeoPoint previous = points.get(index - 1);
            GeoPoint current = points.get(index);

            totalKilometers += haversineKilometers(previous, current);
        }

        String roundedDistance = BigDecimal.valueOf(totalKilometers)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();

        return new TripSummary(
                first.time(),
                last.time(),
                durationSeconds,
                roundedDistance,
                points.size()
        );
    }

    static double haversineKilometers(
            GeoPoint first,
            GeoPoint second
    ) {
        double latitudeDifference = Math.toRadians(
                second.latitude() - first.latitude()
        );

        double longitudeDifference = Math.toRadians(
                second.longitude() - first.longitude()
        );

        double firstLatitude = Math.toRadians(first.latitude());
        double secondLatitude = Math.toRadians(second.latitude());

        double halfChordLengthSquared =
                Math.sin(latitudeDifference / 2.0)
                        * Math.sin(latitudeDifference / 2.0)
                        + Math.cos(firstLatitude)
                        * Math.cos(secondLatitude)
                        * Math.sin(longitudeDifference / 2.0)
                        * Math.sin(longitudeDifference / 2.0);

        halfChordLengthSquared = Math.max(0.0, Math.min(1.0, halfChordLengthSquared));

        double centralAngle = 2.0 * Math.atan2(
                Math.sqrt(halfChordLengthSquared),
                Math.sqrt(1.0 - halfChordLengthSquared)
        );

        return EARTH_RADIUS_KM * centralAngle;
    }
}