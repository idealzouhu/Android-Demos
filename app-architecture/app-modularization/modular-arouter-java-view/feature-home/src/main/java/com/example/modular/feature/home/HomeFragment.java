package com.example.modular.feature.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.modular.base.RouterPath;
import com.example.modular.common.model.NoteItem;
import com.example.modular.common.service.ILastOpenedNoteProvider;
import dagger.hilt.android.AndroidEntryPoint;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页 Tab 内容，在 MainActivity 容器内展示，底部导航栏常驻。
 * 通过 Hilt 注入 ILastOpenedNoteProvider（由 feature-detail 提供实现），展示「最近浏览」实现组件间通信。
 */
@AndroidEntryPoint
@Route(path = RouterPath.HOME_FRAGMENT)
public class HomeFragment extends Fragment {

    @Inject
    ILastOpenedNoteProvider lastOpenedNoteProvider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_activity_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvLastOpened = view.findViewById(R.id.home_tv_last_opened);
        String lastTitle = lastOpenedNoteProvider.getLastOpenedNoteTitle();
        if (lastTitle != null && !lastTitle.isEmpty()) {
            tvLastOpened.setVisibility(View.VISIBLE);
            tvLastOpened.setText("最近浏览：" + lastTitle);
        } else {
            tvLastOpened.setVisibility(View.GONE);
        }

        RecyclerView rv = view.findViewById(R.id.home_rv_list);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<NoteItem> list = buildFakeList();
        HomeAdapter adapter = new HomeAdapter(list, item -> {
            // 传入 context 保证详情页与当前 Activity 同一任务栈，返回时能回到首页并触发 onResume 刷新「最近浏览」
            com.alibaba.android.arouter.launcher.ARouter.getInstance()
                    .build(RouterPath.NOTE_DETAIL)
                    .withString("id", item.getId())
                    .withString("title", item.getTitle())
                    .withString("summary", item.getSummary())
                    .navigation(requireContext());
        });
        rv.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从详情返回时刷新「最近浏览」
        View view = getView();
        if (view != null) {
            TextView tvLastOpened = view.findViewById(R.id.home_tv_last_opened);
            String lastTitle = lastOpenedNoteProvider.getLastOpenedNoteTitle();
            if (lastTitle != null && !lastTitle.isEmpty()) {
                tvLastOpened.setVisibility(View.VISIBLE);
                tvLastOpened.setText("最近浏览：" + lastTitle);
            } else {
                tvLastOpened.setVisibility(View.GONE);
            }
        }
    }

    private List<NoteItem> buildFakeList() {
        List<NoteItem> list = new ArrayList<>();
        list.add(new NoteItem("1", "周末探店 | 咖啡与书", "一家藏在胡同里的独立咖啡馆，手冲很赞。", ""));
        list.add(new NoteItem("2", "秋日穿搭分享", "针织开衫 + 半裙，温柔又显瘦。", ""));
        list.add(new NoteItem("3", "新手烘焙日记", "第一次做戚风，居然没塌。", ""));
        return list;
    }
}
