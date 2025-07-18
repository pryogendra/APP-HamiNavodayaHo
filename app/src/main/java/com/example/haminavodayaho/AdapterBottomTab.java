package com.example.haminavodayaho;

import static com.example.haminavodayaho.FragmentManager.tabTitles;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdapterBottomTab extends FragmentStateAdapter {
    public AdapterBottomTab(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0 ) {
            return new Home();
        } else if (position == 1) {
            return new Inbox();
        } else if (position == 2) {
            return new Event();
        } else if (position == 3) {
            return new Club();
        }else {
            return new Profile();
        }
    }

    @Override
    public int getItemCount() {
        return tabTitles.length;
    }
}
