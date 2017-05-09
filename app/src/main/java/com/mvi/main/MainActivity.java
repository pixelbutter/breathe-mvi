package com.mvi.main;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.mvi.Constants;
import com.mvi.PresenterLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import pixelbutter.com.mvi.R;

public class MainActivity extends AppCompatActivity implements MainContract.View, LoaderManager.LoaderCallbacks<MainPresenter> {

    private static final int LOADER_ID = 101;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.button)
    Button button;

    @BindView(R.id.cityName)
    TextView cityName;

    private MainContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onViewAttached(this);
        setup();
    }

    @Override
    protected void onStop() {
        presenter.onViewDetached();
        super.onStop();
    }


    @Override
    public Loader<MainPresenter> onCreateLoader(int id, Bundle args) {
        return new PresenterLoader<>(this, new MainPresenterFactory());
    }

    @Override
    public void onLoadFinished(Loader<MainPresenter> loader, MainPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLoaderReset(Loader loader) {
        presenter = null;
    }

    private void setup() {
        Observable<SearchAction> actions = RxView.clicks(button)
                .map(ignored -> new SubmitEvent(getSelectedLocation()))
                .map(submitEvent -> new SearchAction(submitEvent.getCityName()));
        presenter.observeSearchActions(actions);
    }

    public void render(MainUiModel model) {
        Log.d(Constants.TAG_THREAD_DEBUGGING, "Render work on: " + Thread.currentThread().toString());
        Log.d("Kao", "inProgress? " + model.isInProgress() + " success? " + model.isSuccess() + " errorMessage " + model.getError());
        progressBar.setVisibility(model.isInProgress() ? View.VISIBLE : View.GONE);
        if (!model.isInProgress()) {
            if (model.isSuccess()) {
                cityName.setText(model.getData().getResults().get(0).getCity());
            } else if (model.getError() != null) {
                cityName.setText(getString(R.string.error_retrieving_data));
            }
        }
    }

    private String getSelectedLocation() {
        String location;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radioOption1:
                location = "Brussels";
                break;
            case R.id.radioOption2:
                location = "London";
                break;
            default:
                location = "";
                break;
        }
        return location;
    }
}
