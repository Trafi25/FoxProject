package com.traffipart.foxapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.traffipart.foxapplication.data.DefaultTripRepository;
import com.traffipart.foxapplication.data.DriveApiClient;
import com.traffipart.foxapplication.data.TripCache;
import com.traffipart.foxapplication.data.TripJsonParser;
import com.traffipart.foxapplication.domain.TripRepository;
import com.traffipart.foxapplication.domain.TripSummary;
import com.traffipart.foxapplication.ui.TripUiState;
import com.traffipart.foxapplication.ui.TripViewModel;

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

    private TripViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();

        FoxApplication application =
                (FoxApplication) getApplication();

        TripRepository repository =
                application
                        .appContainer()
                        .tripRepository();

        viewModel = new TripViewModel(repository);

        viewModel.observe(state ->
                runOnUiThread(() -> render(state))
        );

        retryButton.setOnClickListener(
                view -> viewModel.loadTrip()
        );

        viewModel.loadTrip();
    }

    private void bindViews() {
        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusText);
        contentGroup = findViewById(R.id.contentGroup);
        startTimeValue = findViewById(R.id.startTimeValue);
        endTimeValue = findViewById(R.id.endTimeValue);
        durationValue = findViewById(R.id.durationValue);
        distanceValue = findViewById(R.id.distanceValue);
        errorText = findViewById(R.id.errorText);
        retryButton = findViewById(R.id.retryButton);
    }

    private void render(TripUiState state) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        switch (state.status()) {
            case LOADING -> renderLoading();
            case CONTENT -> renderContent(
                    state.summary(),
                    state.fromCache()
            );
            case ERROR -> renderError(
                    state.errorMessage()
            );
        }
    }

    private void renderLoading() {
        progressBar.setVisibility(View.VISIBLE);

        statusText.setVisibility(View.VISIBLE);
        statusText.setText(
                "Fahrtdaten werden geladen..."
        );

        contentGroup.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        retryButton.setEnabled(false);
    }

    private void renderContent(
            TripSummary summary,
            boolean fromCache
    ) {
        progressBar.setVisibility(View.GONE);
        contentGroup.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);

        startTimeValue.setText(
                summary.startTime() + " UTC"
        );

        endTimeValue.setText(
                summary.endTime() + " UTC"
        );

        durationValue.setText(
                summary.durationSeconds() + " Sekunden"
        );

        distanceValue.setText(
                summary.distanceKilometers() + " km"
        );

        String source = fromCache
                ? "Offline-Daten"
                : "Aktuelle Daten";

        statusText.setVisibility(View.VISIBLE);
        statusText.setText(
                source
                        + " · "
                        + summary.pointCount()
                        + " Ortungspunkte"
        );

        retryButton.setVisibility(
                fromCache ? View.VISIBLE : View.GONE
        );

        retryButton.setEnabled(true);
    }

    private void renderError(String message) {
        progressBar.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
        contentGroup.setVisibility(View.GONE);

        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);

        retryButton.setVisibility(View.VISIBLE);
        retryButton.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        if (viewModel != null) {
            viewModel.clear();
        }

        super.onDestroy();
    }
}