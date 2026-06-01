package com.example.modular;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.modular.base.RouterPath;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * 应用层：壳工程主界面，底部 5 Tab 常驻，Tab 内容在容器内用 Fragment 展示。
 * 使用 @AndroidEntryPoint 以便子 Fragment 可通过 Hilt 注入组件间服务。
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private int currentNavId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.main_bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == currentNavId) return true;
            currentNavId = id;

            String path;
            if (id == R.id.main_nav_home) path = RouterPath.HOME_FRAGMENT;
            else if (id == R.id.main_nav_discover) path = RouterPath.DISCOVER_FRAGMENT;
            else if (id == R.id.main_nav_publish) path = RouterPath.PUBLISH_FRAGMENT;
            else if (id == R.id.main_nav_message) path = RouterPath.MESSAGE_FRAGMENT;
            else if (id == R.id.main_nav_profile) path = RouterPath.PROFILE_FRAGMENT;
            else return false;

            Object obj = ARouter.getInstance().build(path).navigation();
            if (obj instanceof Fragment) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, (Fragment) obj)
                        .commit();
            }
            return true;
        });

        // 默认选首页并加载首页 Fragment
        currentNavId = R.id.main_nav_home;
        bottomNav.setSelectedItemId(R.id.main_nav_home);
        Object defaultFragment = ARouter.getInstance().build(RouterPath.HOME_FRAGMENT).navigation();
        if (defaultFragment instanceof Fragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, (Fragment) defaultFragment)
                    .commit();
        }
    }
}
