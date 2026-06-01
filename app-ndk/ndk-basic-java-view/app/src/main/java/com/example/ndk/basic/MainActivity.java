package com.example.ndk.basic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.jni.cmake.JniDemoCMake;
import com.example.jni.ndkbuild.JniDemoNdkBuild;
import com.example.ndk.basic.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView tv = binding.sampleText;
        String text = JniDemoCMake.getMessageStatic()
                + "\n"
                + JniDemoCMake.getMessageDynamic()
                + "\n\n"
                + JniDemoNdkBuild.getMessageStatic()
                + "\n"
                + JniDemoNdkBuild.getMessageDynamic();
        tv.setText(text);

        binding.crashCmakeButton.setOnClickListener(
                v -> JniDemoCMake.triggerNativeCrashForStackAnalysis());
    }
}
