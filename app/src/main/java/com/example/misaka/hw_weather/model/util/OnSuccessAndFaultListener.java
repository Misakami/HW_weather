package com.example.misaka.hw_weather.model.util;

public interface OnSuccessAndFaultListener {
    void onSuccess(String result);

    void onFault(String errorMsg);
}
