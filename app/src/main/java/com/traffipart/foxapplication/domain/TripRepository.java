package com.traffipart.foxapplication.domain;

public interface TripRepository {

    TripLoadResult load() throws Exception;
}