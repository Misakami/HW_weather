package com.example.misaka.hw_weather.presenter;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;
/**
 * @author misaka
 * @date 2018/12/11
 */
public class WeatherFragmentAdapter extends FragmentPagerAdapter {
    private List<WeatherFragment> fragments;

    public WeatherFragmentAdapter(FragmentManager fm,List<WeatherFragment> fragments ) {
        super(fm);
        this.fragments =  fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public long getItemId(int position) {
        return fragments.get(position).hashCode();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return  POSITION_NONE;
    }


}
