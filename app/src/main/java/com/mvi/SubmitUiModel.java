package com.mvi;

import com.mvi.network.model.MeasurementResponse;

// pojo that represents all data displayed in UI. a.k.a "UIModel"
final class SubmitUiModel extends BaseUiModel<MeasurementResponse> {

    static SubmitUiModel fromResult(SearchResult result) {
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

    static SubmitUiModel idle() {
        return new SubmitUiModel(false, false, null, null);
    }

    static SubmitUiModel inProgress() {
        return new SubmitUiModel(true, false, null, null);
    }

    static SubmitUiModel success(MeasurementResponse data) {
        return new SubmitUiModel(false, true, null, data);
    }

    static SubmitUiModel failure(Throwable error) {
        return new SubmitUiModel(false, false, error, null);
    }

    private SubmitUiModel(boolean inProgress, boolean success, Throwable error, MeasurementResponse data) {
        super(inProgress, success, error, data);
    }
}