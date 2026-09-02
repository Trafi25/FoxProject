package com.traffipart.foxapplication;


import static org.junit.Assert.assertEquals;

import com.traffipart.foxapplication.domain.GeoPoint;
import com.traffipart.foxapplication.domain.TripCalculator;
import com.traffipart.foxapplication.domain.TripSummary;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public final class TripCalculatorTest {

    @Test
    public void summarize_calculatesDuration() {
        GeoPoint first = new GeoPoint(
                "2023-07-02 14:00:23",
                1_000L,
                51.0,
                13.0
        );

        GeoPoint second = new GeoPoint(
                "2023-07-02 14:01:23",
                61_000L,
                51.0,
                13.0
        );

        TripSummary result = TripCalculator.summarize(
                Arrays.asList(first, second)
        );

        assertEquals(60L, result.durationSeconds());
        assertEquals("0.00", result.distanceKilometers());
    }

    @Test
    public void summarize_addsConsecutiveDistances() {
        GeoPoint first = new GeoPoint(
                "start", 0L, 0.0, 0.0
        );

        GeoPoint second = new GeoPoint(
                "middle", 1_000L, 0.0, 1.0
        );

        GeoPoint third = new GeoPoint(
                "end", 2_000L, 0.0, 2.0
        );

        TripSummary result = TripCalculator.summarize(
                Arrays.asList(first, second, third)
        );

        assertEquals("222.39", result.distanceKilometers());
        assertEquals(3, result.pointCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void summarize_rejectsEmptyInput() {
        TripCalculator.summarize(Collections.emptyList());
    }
}