package com.mvi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.jakewharton.rxbinding2.view.RxView;
import com.mvi.network.AqService;
import com.mvi.network.model.MeasurementResponse;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pixelbutter.com.mvi.R;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    // Preface to illustrate the basic separation of parts without transformers/actions/etc.
    private void setupPreface() {
        // Part 1: take ui events and turns them into stream to react on.
        // event --> Observable<event>.
        Observable<SubmitEvent> events = RxView.clicks(button)
                .map(ignored -> new SubmitEvent(getSelectedLocation()));

        // Part 2: take stream of events and does something async. Produces a single viewstate to bind our UI to.
        // Doesn't know about UI - shown
        Observable<SubmitUiModel> models = events
                // map() would emit Observable<Observable<Response>>
                .flatMap(event -> getLocationObservable(event.getCityName())
                        .subscribeOn(Schedulers.io())
                        .delay(2, TimeUnit.SECONDS)
                        // reacts on Observable<MeasurementResponse> response. Maps to Observable<BaseUiModel>.
                        .map(SubmitUiModel::success)
                        // reacts on error notification from Observable<MeasurementResponse>. Maps to Observable<BaseUiModel>.
                        .onErrorReturn(SubmitUiModel::failure)
                        // emits Observable<BaseUiModel> in progress state before emitting anything else
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(SubmitUiModel.inProgress()));

        // Part 3: Takes model/viewstate, subscribes to updates from it, and binds it back to ui.
        disposables.add(models.subscribe(this::render, Throwable::printStackTrace));
    }

    private void setupPrefaceWithDecoupling() {
        // Part 1: take ui events/intentions and turns them into a single stream of actions (e.g. submit clicks to search actions)
        // click --> Observable<SubmitEvent> --> Observable<SearchAction>
        Observable<SearchAction> actions = RxView.clicks(button)
                .map(ignored -> new SubmitEvent(getSelectedLocation()))
                .map(submitEvent -> new SearchAction(submitEvent.getCityName()));

        // Part 2: Takes stream of actions and does something async. Produces Observable<Result>
        Observable<SearchResult> results = actions
                .flatMap(action -> action.getLocationObservable()
                        .subscribeOn(Schedulers.io())
                        .delay(2, TimeUnit.SECONDS)
                        // reacts on Observable<MeasurementResponse> response. Maps to Observable<Result>.
                        .map(SearchResult::success)
                        .onErrorReturn(SearchResult::failure)
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(SearchResult.inProgress()));

        // Part 3: Takes model results and maps back to UIModel/View state
        SubmitUiModel initialState = SubmitUiModel.idle();
        Observable<SubmitUiModel> uiModels = results
                .scan(initialState, (state, result) -> SubmitUiModel.fromResult(result));

        // Part 3: Takes model/viewstate, subscribes to updates from it, and binds it back to ui.
        disposables.add(uiModels.subscribe(this::render, Throwable::printStackTrace));
    }

    // Separate sections using transformers to illustrate that the middle section observe/know nothing about the UI
    // This way all the UI code is grouped together and just uses the transformer.
    private void setupWithTransformers() {
        // Part 1: now grouped with other UI/part3

        // Part 2: Transformer defined independent of events instance. Parameterized with some Observable<SubmitEvent>
        ObservableTransformer<SubmitEvent, SubmitUiModel> mainTransformer = mainEvents -> mainEvents
                .flatMap(event -> getLocationObservable(event.getCityName())
                        .subscribeOn(Schedulers.io())
                        .delay(3, TimeUnit.SECONDS)
                        .map(SubmitUiModel::success)
                        .onErrorReturn(SubmitUiModel::failure)
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(SubmitUiModel.inProgress()));

        // Part 3: Groups all the UI code together.
        disposables.add(
                // Part 1 from original preface
                RxView.clicks(button)
                        .map(ignored -> new SubmitEvent(getSelectedLocation()))
                        // Apply transformer function to turn Observable<SubmitEvent> to Observable<BaseUiModel>
                        .compose(mainTransformer)
                        // original part 3
                        .subscribe(this::render, Throwable::printStackTrace));
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

    private Observable<MeasurementResponse> getLocationObservable(String location) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openaq.org/v1/")
                .client(client)
                .build();

        AqService service = retrofit.create(AqService.class);
        return service.getLocation(location, 1);
    }
}
