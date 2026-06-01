package com.viewpage2.basic.ui.reflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.viewpage2.basic.R;
import com.viewpage2.basic.databinding.FragmentReflowBinding;

/**
 * Reflow 页面：使用 ViewPager2 实现滑动视图，配合 TabLayout 显示标签页。
 */
public class ReflowFragment extends Fragment {

    private FragmentReflowBinding binding;
    private ReflowCollectionAdapter reflowCollectionAdapter;
    private ViewPager2 viewPager;
    private TabLayoutMediator tabLayoutMediator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReflowBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // ViewPager2 使用 FragmentStateAdapter 提供页面
        reflowCollectionAdapter = new ReflowCollectionAdapter(this);
        viewPager = binding.pager;
        viewPager.setAdapter(reflowCollectionAdapter);

        // TabLayout 与 ViewPager2 联动：同步选中、点击切换
        TabLayout tabLayout = binding.tabLayout;
        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(getString(R.string.reflow_page_label, position + 1)));
        tabLayoutMediator.attach();
    }

    @Override
    public void onDestroyView() {
        // 必须 detach，避免泄漏
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }
        binding = null;
        super.onDestroyView();
    }
}
