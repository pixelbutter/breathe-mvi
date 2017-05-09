package com.mvi.main;

import com.mvi.BasePresenter;

import io.reactivex.Observable;

public interface MainContract {

    interface View {
        void render(MainUiModel uiModel);
    }

    interface Presenter extends BasePresenter<View> {
        void observeSearchActions(Observable<SearchAction> searchActions);
    }
}
