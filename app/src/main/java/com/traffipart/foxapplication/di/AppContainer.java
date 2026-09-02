package com.traffipart.foxapplication.di;

import com.traffipart.foxapplication.domain.repository.TripRepository;

public interface AppContainer {

    TripRepository tripRepository();
}