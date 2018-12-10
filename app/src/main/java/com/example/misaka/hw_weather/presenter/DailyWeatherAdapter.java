package com.example.misaka.hw_weather.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.misaka.hw_weather.R;
import com.example.misaka.hw_weather.model.util.ItemTouchHelperAdapter;

import java.util.Collections;
import java.util.List;

/**
 * @author misaka
 * @date 2018/12/8
 */
public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private Context context;
    private List<Dailyweather> dailyweathers;

    public DailyWeatherAdapter(List<Dailyweather> dailyweathers){
        this.dailyweathers = dailyweathers;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null){
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Dailyweather dailyweather = dailyweathers.get(i);
        viewHolder.temptext.setText(dailyweather.getTemp());
        viewHolder.condtext.setText(dailyweather.getCond());
        Glide.with(context).asDrawable().load(dailyweather.getImageid())
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return dailyweathers.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //交换位置
        Collections.swap(dailyweathers,fromPosition,toPosition);
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemDissmiss(int position) {
        //移除数据
        dailyweathers.remove(position);
        notifyItemRemoved(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView temptext;
        TextView condtext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            temptext = itemView.findViewById(R.id.temps);
            condtext = itemView.findViewById(R.id.cond);
            imageView = itemView.findViewById(R.id.pic);
        }
    }
}
