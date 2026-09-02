package com.traffipart.foxapplication.data;

import com.traffipart.foxapplication.domain.GeoPoint;
import com.traffipart.foxapplication.domain.TripCalculator;
import com.traffipart.foxapplication.domain.TripSummary;

import java.io.IOException;
import java.util.List;

public final class TripRepository {

    private final DriveApiClient apiClient;
    private final TripCache cache;
    private final TripJsonParser parser;

    public TripRepository(
            DriveApiClient apiClient,
            TripCache cache,
            TripJsonParser parser
    ) {
        this.apiClient = apiClient;
        this.cache = cache;
        this.parser = parser;
    }

    public LoadResult load() throws Exception {
        Exception networkFailure;

        try {
            String liveJson = apiClient.download();
            List<GeoPoint> livePoints = parser.parse(liveJson);

            TripSummary summary =
                    TripCalculator.summarize(livePoints);

            try {
                cache.save(liveJson);
            } catch (IOException ignored) {
                // Live data can still be displayed if caching fails.
            }

            return new LoadResult(summary, false);
        } catch (Exception exception) {
            networkFailure = exception;
        }

        try {
            String cachedJson = cache.read();

            if (cachedJson != null) {
                List<GeoPoint> cachedPoints =
                        parser.parse(cachedJson);

                TripSummary summary =
                        TripCalculator.summarize(cachedPoints);

                return new LoadResult(summary, true);
            }
        } catch (Exception cacheFailure) {
            networkFailure.addSuppressed(cacheFailure);
        }

        throw networkFailure;
    }

    public record LoadResult(
            TripSummary summary,
            boolean fromCache
    ) {
    }
}