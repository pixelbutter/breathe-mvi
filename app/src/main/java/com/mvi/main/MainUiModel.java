package com.mvi.main;

import com.mvi.BaseUiModel;
import com.mvi.network.model.MeasurementResponse;

// pojo that represents all data displayed in UI. a.k.a "UIModel"
final class MainUiModel extends BaseUiModel<MeasurementResponse> {

    static MainUiModel fromResult(SearchResult result) {
        switch (result.state) {
            case IN_PROGRESS:
                return inProgress();
            case SUCCESS:
                return success(result.getData());
            case ERROR:
                return failure(result.getError());
            default:
                return idle();
        }
    }

    static MainUiModel idle() {
        return new MainUiModel(false, false, null, null);
    }

    static MainUiModel inProgress() {
        return new MainUiModel(true, false, null, null);
    }

    static MainUiModel success(MeasurementResponse data) {
        return new MainUiModel(false, true, null, data);
    }

    static MainUiModel failure(Throwable error) {
        return new MainUiModel(false, false, error, null);
    }

    private MainUiModel(boolean inProgress, boolean success, Throwable error, MeasurementResponse data) {
        super(inProgress, success, error, data);
    }
}