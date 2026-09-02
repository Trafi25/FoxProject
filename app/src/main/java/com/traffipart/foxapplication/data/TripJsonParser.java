package com.traffipart.foxapplication.data;

import com.traffipart.foxapplication.domain.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public final class TripJsonParser {

    private static final String TIME_PATTERN =
            "yyyy-MM-dd HH:mm:ss";

    public List<GeoPoint> parse(String json) throws JSONException {
        JSONArray array = new JSONArray(json);
        List<GeoPoint> points = new ArrayList<>();

        for (int index = 0; index < array.length(); index++) {
            JSONObject item = array.optJSONObject(index);

            if (item == null) {
                continue;
            }

            try {
                String time = String.valueOf(
                        item.get("time")
                ).trim();

                double latitude = Double.parseDouble(
                        String.valueOf(item.get("latitude"))
                );

                double longitude = Double.parseDouble(
                        String.valueOf(item.get("longitude"))
                );

                long timestampMillis = parseUtcTime(time);

                if (!coordinatesAreValid(latitude, longitude)) {
                    continue;
                }

                points.add(new GeoPoint(
                        time,
                        timestampMillis,
                        latitude,
                        longitude
                ));
            } catch (JSONException | IllegalArgumentException ignored) {
            }
        }

        if (points.isEmpty()) {
            throw new JSONException(
                    "No valid location points found"
            );
        }

        points.sort(
                Comparator.comparingLong(GeoPoint::timestampMillis)
        );

        return points;
    }

    private boolean coordinatesAreValid(
            double latitude,
            double longitude
    ) {
        return latitude >= -90.0
                && latitude <= 90.0
                && longitude >= -180.0
                && longitude <= 180.0;
    }

    private long parseUtcTime(String value) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                TIME_PATTERN,
                Locale.ROOT
        );

        formatter.setLenient(false);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        ParsePosition position = new ParsePosition(0);
        Date parsedDate = formatter.parse(value, position);

        if (parsedDate == null
                || position.getIndex() != value.length()) {
            throw new IllegalArgumentException(
                    "Invalid UTC timestamp: " + value
            );
        }

        return parsedDate.getTime();
    }
}