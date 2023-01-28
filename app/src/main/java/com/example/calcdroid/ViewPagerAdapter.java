package com.example.calcdroid;

import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: {
                return new ViewPagerFragment(R.layout.equations_tab);
            }
            case 1: {
                return new ViewPagerFragment(R.layout.graphs_tab);
            }
        }
        return null;
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
