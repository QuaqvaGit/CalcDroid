package com.example.calcdroid;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    static String[] TAB_NAMES = new String[] {"Уравнения", "Графики"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_CalcDroid);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        setupTabs();
    }

    void setupTabs() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        ViewPager2 pager = findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, pager,
                (tab, position) -> tab.setText(TAB_NAMES[position])
        ).attach();
    }
}