package com.example.modular.feature.detail;

import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.modular.base.BaseActivity;
import com.example.modular.base.RouterPath;
import com.example.modular.common.service.ILastOpenedNoteProvider;
import dagger.hilt.android.AndroidEntryPoint;

import javax.inject.Inject;

/**
 * 业务组件层：笔记/内容详情页，从首页或发现通过 ARouter 跳转进入。
 * 通过 Hilt 注入 ILastOpenedNoteProvider，在打开详情时写入「最近打开」，供首页等模块展示。
 */
@AndroidEntryPoint
@Route(path = RouterPath.NOTE_DETAIL)
public class DetailActivity extends BaseActivity {

    @Inject
    ILastOpenedNoteProvider lastOpenedNoteProvider;

    @Autowired(name = "id")
    public String id;

    @Autowired(name = "title")
    public String title;

    @Autowired(name = "summary")
    public String summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.detail_activity_detail);

        if (title != null) {
            lastOpenedNoteProvider.setLastOpenedNoteTitle(title);
        }

        TextView tvTitle = findViewById(R.id.detail_title);
        TextView tvSummary = findViewById(R.id.detail_summary);
        TextView tvId = findViewById(R.id.detail_id);

        tvTitle.setText(title != null ? title : "笔记详情");
        tvSummary.setText(summary != null ? summary : "");
        tvId.setText("ID: " + (id != null ? id : "-"));
    }
}
