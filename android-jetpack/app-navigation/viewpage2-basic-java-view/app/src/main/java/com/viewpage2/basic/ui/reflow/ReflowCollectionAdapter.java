package com.viewpage2.basic.ui.reflow;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * 为 Reflow 中的 ViewPager2 提供页面的 FragmentStateAdapter。
 */
public class ReflowCollectionAdapter extends FragmentStateAdapter {

    private static final int PAGE_COUNT = 5;

    public ReflowCollectionAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    /** 每页对应一个 ReflowObjectFragment，position 以 1 起传参 */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new ReflowObjectFragment();
        Bundle args = new Bundle();
        args.putInt(ReflowObjectFragment.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }
}
