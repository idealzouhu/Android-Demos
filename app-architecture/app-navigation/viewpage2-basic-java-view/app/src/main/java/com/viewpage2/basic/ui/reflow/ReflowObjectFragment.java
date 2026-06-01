package com.viewpage2.basic.ui.reflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.viewpage2.basic.R;

/**
 * 代表 Reflow 滑动视图中某一页的 Fragment。
 */
public class ReflowObjectFragment extends Fragment {

    /** 用于从 arguments 取当前页码（从 1 开始） */
    public static final String ARG_OBJECT = "object";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reflow_object, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_OBJECT)) {
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(getString(R.string.reflow_page_label, args.getInt(ARG_OBJECT)));
        }
    }
}
