package com.mvi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pixelbutter.com.mvi.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.button)
    Button button;

    @BindView(R.id.cityName)
    TextView cityName;

    CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupFinal();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

    private void setupFinal() {
        Observable<SearchAction> actions = RxView.clicks(button)
                .map(ignored -> new SubmitEvent(getSelectedLocation()))
                .map(submitEvent -> new SearchAction(submitEvent.getCityName()));

        ObservableTransformer<SearchAction, SearchResult> submitTransformer = mainEvents -> mainEvents
                .flatMap(action -> action.getLocationObservable()
                        .subscribeOn(Schedulers.io())
                        .delay(3, TimeUnit.SECONDS)
                        .map(SearchResult::success)
                        .onErrorReturn(SearchResult::failure)
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(SearchResult.inProgress()));

        disposables.add(actions
                .compose(submitTransformer)
                .scan(SubmitUiModel.idle(), (state, result) -> SubmitUiModel.fromResult(result))
                .subscribe(this::render, Throwable::printStackTrace));
    }

    private void render(SubmitUiModel model) {
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
