package com.example.misaka.hw_weather.Presenter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misaka.hw_weather.R;
import com.example.misaka.hw_weather.model.db.City;
import com.example.misaka.hw_weather.model.db.County;
import com.example.misaka.hw_weather.model.db.Province;
import com.example.misaka.hw_weather.model.util.Httpclient;
import com.example.misaka.hw_weather.model.util.Utility;
import com.example.misaka.hw_weather.view.WeatherActivity;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button button;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;
    private List<County> countyList;
    private List<City> cityList;

    private Province selectedProvince;
    private City selectetedCity;
    private County selectedCounty;

    private int level;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title);
        button = (Button) view.findViewById(R.id.back);
        listView = (ListView) view.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (level == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCity();
                } else if (level == LEVEL_CITY) {
                    selectetedCity = cityList.get(position);
                    queryCounty();
                }else if (level == LEVEL_COUNTY){
                    selectedCounty = countyList.get(position);
                    WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                    weatherActivity.drawerLayout.closeDrawers();
                    weatherActivity.addList(selectedCounty.getWeatherid());
                    level = LEVEL_PROVINCE;
                    queryProvinces();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (level == LEVEL_COUNTY) {
                    queryCity();
                } else if (level == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
        titleText.setText("中国");
        button.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceNmae());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            level = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, LEVEL_PROVINCE);
        }
    }

    private void queryCity() {
        titleText.setText(selectedProvince.getProvinceNmae());
        button.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceID = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            level = LEVEL_CITY;
        } else {
            String address = "http://guolin.tech/api/china/" + String.valueOf(selectedProvince.getProvinceCode());
            queryFromServer(address, LEVEL_CITY);
        }
    }

    private void queryCounty() {
        titleText.setText(selectetedCity.getCityName());
        countyList = LitePal.where("Cityid = ?", String.valueOf(selectetedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            level = LEVEL_COUNTY;
        } else {
            String address ="http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()+"/"+selectetedCity.getCityCode();
            queryFromServer(address,LEVEL_COUNTY);
        }
    }

    private void queryFromServer(String address, final int Type) {
        showprogress();
        OkHttpClient okHttpClient = Httpclient.instance.getClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            boolean result = false;

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeprogress();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                if (Type == LEVEL_PROVINCE) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if (Type == LEVEL_CITY) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if (Type == LEVEL_COUNTY) {
                    result = Utility.handleCountyResponse(responseText, selectetedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeprogress();
                            if(Type == LEVEL_PROVINCE){
                                queryProvinces();
                            }else if(Type == LEVEL_CITY){
                                queryCity();
                            }else if (Type == LEVEL_COUNTY){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showprogress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeprogress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
