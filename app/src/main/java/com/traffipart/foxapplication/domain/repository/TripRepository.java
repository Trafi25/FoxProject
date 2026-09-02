package com.traffipart.foxapplication.domain.repository;

import com.traffipart.foxapplication.domain.data.TripLoadResult;

public interface TripRepository {

    TripLoadResult load() throws Exception;
}