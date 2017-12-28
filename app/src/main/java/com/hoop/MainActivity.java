package com.hoop;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hoop.widget.HoopChartView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final HoopChartView hoopChartView = findViewById(R.id.hoopChartView);
        findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<HoopChartView.HoopChart> datas = new ArrayList<>();
                datas.add(new HoopChartView.HoopChart("玩游戏", 0.15f, Color.parseColor("#0FF001")));
                datas.add(new HoopChartView.HoopChart("参加培训班", 0.1f, Color.parseColor("#FFF001")));
                datas.add(new HoopChartView.HoopChart("旅游", 0.15f, Color.parseColor("#FF0101")));
                datas.add(new HoopChartView.HoopChart("睡觉", 0.2f, Color.parseColor("#0199FF")));
                datas.add(new HoopChartView.HoopChart("逛商场", 0.15f, Color.parseColor("#FF01FF")));
                datas.add(new HoopChartView.HoopChart("游泳健身", 0.25f, Color.parseColor("#01FFFF")));
                hoopChartView.refresh(datas);
            }
        });
    }
}