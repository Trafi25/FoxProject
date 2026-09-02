package com.traffipart.foxapplication;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.traffipart.foxapplication.data.DriveApiClient;
import com.traffipart.foxapplication.data.TripCache;
import com.traffipart.foxapplication.data.TripJsonParser;
import com.traffipart.foxapplication.data.TripRepository;
import com.traffipart.foxapplication.domain.TripSummary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainActivity extends Activity {

    private ProgressBar progressBar;
    private TextView statusText;
    private LinearLayout contentGroup;
    private TextView startTimeValue;
    private TextView endTimeValue;
    private TextView durationValue;
    private TextView distanceValue;
    private TextView errorText;
    private Button retryButton;

    private ExecutorService executor;
    private Handler mainHandler;
    private TripRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusText);
        contentGroup = findViewById(R.id.contentGroup);
        startTimeValue = findViewById(R.id.startTimeValue);
        endTimeValue = findViewById(R.id.endTimeValue);
        durationValue = findViewById(R.id.durationValue);
        distanceValue = findViewById(R.id.distanceValue);
        errorText = findViewById(R.id.errorText);
        retryButton = findViewById(R.id.retryButton);

        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        repository = new TripRepository(
                new DriveApiClient(),
                new TripCache(getApplicationContext()),
                new TripJsonParser()
        );

        retryButton.setOnClickListener(view -> loadTrip());

        loadTrip();
    }

    private void loadTrip() {
        showLoading();

        executor.execute(() -> {
            try {
                TripRepository.LoadResult result =
                        repository.load();

                mainHandler.post(() -> showResult(result));
            } catch (Exception exception) {
                mainHandler.post(this::showError);
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.VISIBLE);
        statusText.setText("Fahrtdaten werden geladen...");

        contentGroup.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        retryButton.setEnabled(false);
    }

    private void showResult(
            TripRepository.LoadResult result
    ) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        TripSummary summary = result.summary();

        progressBar.setVisibility(View.GONE);
        statusText.setVisibility(View.VISIBLE);
        contentGroup.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);

        startTimeValue.setText(summary.startTime() + " UTC");
        endTimeValue.setText(summary.endTime() + " UTC");

        durationValue.setText(
                summary.durationSeconds() + " Sekunden"
        );

        distanceValue.setText(
                summary.distanceKilometers() + " km"
        );

        String source = result.fromCache()
                ? "Offline-Daten"
                : "Aktuelle Daten";

        statusText.setText(
                source + " · " + summary.pointCount()
                        + " Ortungspunkte"
        );

        retryButton.setVisibility(
                result.fromCache() ? View.VISIBLE : View.GONE
        );

        retryButton.setEnabled(true);
    }

    private void showError() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        progressBar.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
        contentGroup.setVisibility(View.GONE);

        errorText.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
        retryButton.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (executor != null) {
            executor.shutdownNow();
        }
    }
}