package com.traffipart.foxapplication;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import com.traffipart.foxapplication.domain.TripLoadResult;
import com.traffipart.foxapplication.domain.TripRepository;
import com.traffipart.foxapplication.domain.TripSummary;
import com.traffipart.foxapplication.ui.TripUiState;
import com.traffipart.foxapplication.ui.TripViewModel;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class TripViewModelTest {

    @Test
    public void loadTrip_emitsContentFromRepository()
            throws InterruptedException {

        TripSummary summary = new TripSummary(
                "2023-07-02 14:00:23",
                "2023-07-02 14:14:22",
                839L,
                "20.03",
                15
        );

        TripRepository fakeRepository =
                () -> new TripLoadResult(summary, false);

        TripViewModel viewModel =
                new TripViewModel(fakeRepository);

        AtomicReference<TripUiState> contentState =
                new AtomicReference<>();

        CountDownLatch latch = new CountDownLatch(1);

        viewModel.observe(state -> {
            if (state.status()
                    == TripUiState.Status.CONTENT) {
                contentState.set(state);
                latch.countDown();
            }
        });

        viewModel.loadTrip();

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertNotNull(contentState.get());

        assertEquals(
                839L,
                contentState.get()
                        .summary()
                        .durationSeconds()
        );

        assertEquals(
                "20.03",
                contentState.get()
                        .summary()
                        .distanceKilometers()
        );

        viewModel.clear();
    }

    @Test
    public void loadTrip_emitsErrorWhenRepositoryFails()
            throws InterruptedException {

        TripRepository fakeRepository = () -> {
            throw new IllegalStateException(
                    "Test failure"
            );
        };

        TripViewModel viewModel =
                new TripViewModel(fakeRepository);

        AtomicReference<TripUiState> errorState =
                new AtomicReference<>();

        CountDownLatch latch = new CountDownLatch(1);

        viewModel.observe(state -> {
            if (state.status()
                    == TripUiState.Status.ERROR) {
                errorState.set(state);
                latch.countDown();
            }
        });

        viewModel.loadTrip();

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertNotNull(errorState.get());

        assertEquals(
                "Keine Fahrtdaten verfügbar.",
                errorState.get().errorMessage()
        );

        viewModel.clear();
    }
}