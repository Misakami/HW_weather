package com.example.misaka.hw_weather.model.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

import static android.support.constraint.Constraints.TAG;

public class OnSuccessAndFaultSub extends DisposableObserver<ResponseBody> {

    private int showPaogress = View.VISIBLE;
    private OnSuccessAndFaultListener mOnSuccessAndFaultListener;

    private Context context;
    private ProgressBar progressBar;

    /**
     * @param mOnSuccessAndFaultListener 成功回调监听
     */
    public OnSuccessAndFaultSub(OnSuccessAndFaultListener mOnSuccessAndFaultListener) {
        this.mOnSuccessAndFaultListener = mOnSuccessAndFaultListener;
    }

    /**
     * @param mOnSuccessAndFaultListener 成功回调监听
     * @param context                    上下文
     */
    public OnSuccessAndFaultSub(OnSuccessAndFaultListener mOnSuccessAndFaultListener, Context context) {
        this.mOnSuccessAndFaultListener = mOnSuccessAndFaultListener;
        this.context = context;
    }

    /**
     * @param mOnSuccessAndFaultListener 成功回调监听
     * @param context                    上下文
     * @param showProgress               是否需要显示默认Loading
     */
    public OnSuccessAndFaultSub(OnSuccessAndFaultListener mOnSuccessAndFaultListener, Context context, boolean showProgress) {
        this.mOnSuccessAndFaultListener = mOnSuccessAndFaultListener;
        this.context = context;
    }


    @Override
    public void onNext(ResponseBody responseBody) {
        Log.e(TAG, "onNext: "+responseBody );
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(responseBody.toString());
            int resultCode = jsonObject.getInt("ErrorCode");
            if (resultCode == 1) {
                mOnSuccessAndFaultListener.onSuccess(responseBody.toString());
            } else {
                String errorMsg = jsonObject.getString("ErrorMessage");
                mOnSuccessAndFaultListener.onFault(errorMsg);
                Log.e("OnSuccessAndFaultSub", "errorMsg: " + errorMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(Throwable e) {
        try {
            if (e instanceof SocketTimeoutException) {
            } else if (e instanceof ConnectException) {
                mOnSuccessAndFaultListener.onFault("网络连接超时");
            } else if (e instanceof SSLHandshakeException) {
                mOnSuccessAndFaultListener.onFault("安全证书异常");
            } else if (e instanceof HttpException) {
                int code = ((HttpException) e).code();
                if (code == 504) {
                    mOnSuccessAndFaultListener.onFault("网络异常，请检查您的网络状态");
                } else if (code == 404) {
                    mOnSuccessAndFaultListener.onFault("请求的地址不存在");
                } else {
                    mOnSuccessAndFaultListener.onFault("请求失败");
                }
            } else if (e instanceof UnknownHostException) {
                mOnSuccessAndFaultListener.onFault("域名解析失败");
            } else {
                mOnSuccessAndFaultListener.onFault("error:" + e.getMessage());
            }
        } catch (
                Exception e2) {
            e2.printStackTrace();
        } finally {
            Log.e("OnSuccessAndFaultSub", "error:" + e.getMessage());
        }
    }


    @Override
    public void onComplete() {
        this.dispose();
    }
}
