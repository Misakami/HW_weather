package com.example.misaka.hw_weather.model.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.misaka.hw_weather.R;
/**
 * @author misaka
 * @date 2018/12/8
 */
public class ButtomDilog {
    private Context context;
    private AlertDialog.Builder builder;
    private View view;
    private Button takePhoto;
    private Button album;
    private Button cancel;
    private AlertDialog dialog;

    public ButtomDilog(Context context) {
        this.context = context;
        builder =new AlertDialog.Builder(context);
        view = LayoutInflater.from(context).inflate(R.layout.layout_buttomstyle,null);
        takePhoto = (Button) view.findViewById(R.id.take_photo);
        album = (Button) view.findViewById(R.id.album);
        cancel = (Button) view.findViewById(R.id.cancel);
        dialog = new AlertDialog.Builder(context).create();
    }

    public void setfirsr(View.OnClickListener listener){
        takePhoto.setOnClickListener(listener);
    }
    public void setsecond(View.OnClickListener listener){
        album.setOnClickListener(listener);
    }
    public void show(){
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window window =dialog.getWindow();
        window.setContentView(view);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.animation_buttom);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    public void close(){
        dialog.dismiss();
    }
}
