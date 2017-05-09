package com.mvi.main;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

    private MainUiModel viewState;
    private MainContract.View mainView;
    private CompositeDisposable subscriptions;

    public MainPresenter() {
        subscriptions = new CompositeDisposable();
        viewState = MainUiModel.idle();
    }

    @Override
    public void onViewAttached(MainContract.View mainView) {
        this.mainView = mainView;
        mainView.render(viewState);
    }

    @Override
    public void onViewDetached() {
        subscriptions.dispose();
    }

    @Override
    public void onDestroyed() {
    }

    @Override
    public void observeSearchActions(Observable<SearchAction> actions) {
        // todo convert actions to Subject
        ObservableTransformer<SearchAction, SearchResult> submitTransformer = mainEvents -> mainEvents
                .flatMap(action -> action.getLocationObservable()
                        .subscribeOn(Schedulers.io())
                        .delay(2, TimeUnit.SECONDS)
                        .map(SearchResult::success)
                        .onErrorReturn(SearchResult::failure)
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(SearchResult.inProgress()));

        subscriptions.add(actions
                .compose(submitTransformer)
                .replay(1).autoConnect()
                .scan(viewState, (state, result) -> MainUiModel.fromResult(result))
                .subscribe(state -> { mainView.render(state); viewState = state;}, Throwable::printStackTrace));
    }
}