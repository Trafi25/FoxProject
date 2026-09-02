package com.traffipart.foxapplication.di;

import android.content.Context;
import com.traffipart.foxapplication.data.DefaultTripRepository;
import com.traffipart.foxapplication.data.DriveApiClient;
import com.traffipart.foxapplication.data.TripCache;
import com.traffipart.foxapplication.data.TripJsonParser;
import com.traffipart.foxapplication.domain.TripRepository;

public final class DefaultAppContainer
        implements AppContainer {

    private final TripRepository tripRepository;

    public DefaultAppContainer(Context context) {
        Context applicationContext =
                context.getApplicationContext();

        tripRepository = new DefaultTripRepository(
                new DriveApiClient(),
                new TripCache(applicationContext),
                new TripJsonParser()
        );
    }

    @Override
    public TripRepository tripRepository() {
        return tripRepository;
    }
}