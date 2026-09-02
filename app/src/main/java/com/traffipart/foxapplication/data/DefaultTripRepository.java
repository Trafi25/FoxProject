package com.traffipart.foxapplication.data;

import com.traffipart.foxapplication.domain.GeoPoint;
import com.traffipart.foxapplication.domain.TripCalculator;
import com.traffipart.foxapplication.domain.TripSummary;
import java.io.IOException;
import java.util.List;
import com.traffipart.foxapplication.domain.TripLoadResult;
import com.traffipart.foxapplication.domain.TripRepository;
public final class DefaultTripRepository
        implements TripRepository {

    private final DriveApiClient apiClient;
    private final TripCache cache;
    private final TripJsonParser parser;

    public DefaultTripRepository(
            DriveApiClient apiClient,
            TripCache cache,
            TripJsonParser parser
    ) {
        this.apiClient = apiClient;
        this.cache = cache;
        this.parser = parser;
    }

    @Override
    public TripLoadResult load() throws Exception {
        Exception networkFailure;

        try {
            String liveJson = apiClient.download();

            List<GeoPoint> livePoints =
                    parser.parse(liveJson);

            TripSummary summary =
                    TripCalculator.summarize(livePoints);

            try {
                cache.save(liveJson);
            } catch (IOException ignored) {
                // Valid live data can still be displayed.
            }

            return new TripLoadResult(
                    summary,
                    false
            );
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

                return new TripLoadResult(
                        summary,
                        true
                );
            }
        } catch (Exception cacheFailure) {
            networkFailure.addSuppressed(cacheFailure);
        }

        throw networkFailure;
    }
}