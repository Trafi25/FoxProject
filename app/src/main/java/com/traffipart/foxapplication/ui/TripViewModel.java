package com.traffipart.foxapplication.ui;


import com.traffipart.foxapplication.domain.data.TripLoadResult;
import com.traffipart.foxapplication.domain.repository.TripRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TripViewModel {

    public interface Observer {
        void onStateChanged(TripUiState state);
    }

    private final TripRepository repository;
    private final ExecutorService executor;

    private volatile Observer observer;
    private volatile boolean loading;

    public TripViewModel(TripRepository repository) {
        this(
                repository,
                Executors.newSingleThreadExecutor()
        );
    }

    TripViewModel(
            TripRepository repository,
            ExecutorService executor
    ) {
        this.repository = repository;
        this.executor = executor;
    }

    public void observe(Observer observer) {
        this.observer = observer;
    }

    public void loadTrip() {
        if (loading) {
            return;
        }

        loading = true;
        emit(TripUiState.loading());

        executor.execute(() -> {
            try {
                TripLoadResult result = repository.load();

                emit(TripUiState.content(
                        result.summary(),
                        result.fromCache()
                ));
            } catch (Exception exception) {
                emit(TripUiState.error(
                        "Keine Fahrtdaten verfügbar."
                ));
            } finally {
                loading = false;
            }
        });
    }

    private void emit(TripUiState state) {
        Observer currentObserver = observer;

        if (currentObserver != null) {
            currentObserver.onStateChanged(state);
        }
    }

    public void clear() {
        observer = null;
        executor.shutdownNow();
    }
}