package com.luren.mydashboardprogress;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.luren.dashboardprogressbarlib.DashboardProgressbar;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private DashboardProgressbar dpb;
    private TextView tvText;
    private double current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvText = findViewById(R.id.tv_text);
        dpb = findViewById(R.id.dpb);
        dpb.setProgressData(100, 50);
        dpb.addProgressColor(Color.BLUE);
        dpb.addProgressColor(Color.CYAN);
        dpb.addProgressColor(Color.RED);
        dpb.addProgressColor(Color.YELLOW);
        dpb.setmAnimationListener(new DashboardProgressbar.OnDataUpdateListener() {
            @Override
            public void onDataUpdate(float progress) {
                tvText.setText("当前值" + 100 * progress);
            }

            @Override
            public void onAnimComplete() {
                tvText.setText("当前值" + current);

            }
        });
    }

    public void onRandom(View view) {
        current = new Random().nextDouble() * 100;
        dpb.setmCurrentData(current);
    }
}
