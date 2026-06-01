package com.example.hce.pos;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hce.pos.iccsim.IccHceDebugState;
import com.example.hce.pos.iccsim.IccScript;
import com.example.hce.pos.iccsim.IccScriptLoader;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView textCapabilities;
    private TextView textScriptStatus;
    private TextView textSessionStep;
    /** 本地解析脚本得到的步数，用于 HostApduService 尚未启动时的界面展示。 */
    private int cachedScriptStepCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textCapabilities = findViewById(R.id.textCapabilities);
        textScriptStatus = findViewById(R.id.textScriptStatus);
        textSessionStep = findViewById(R.id.textSessionStep);

        findViewById(R.id.buttonNfcPaymentSettings).setOnClickListener(v -> openNfcPaymentSettings());
        findViewById(R.id.buttonNfcSettings).setOnClickListener(v -> openNfcSettings());

        refreshCapabilities();
        refreshScriptStatusFromAssets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCapabilities();
        refreshScriptStatusFromAssets();
        refreshSessionStepUi();
    }

    private void refreshCapabilities() {
        PackageManager pm = getPackageManager();
        boolean nfc = pm.hasSystemFeature(PackageManager.FEATURE_NFC);
        boolean hce = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
        String nfcLine = getString(nfc ? R.string.status_nfc_supported : R.string.status_nfc_unsupported);
        String hceLine = getString(hce ? R.string.status_hce_supported : R.string.status_hce_unsupported);
        textCapabilities.setText(nfcLine + "\n" + hceLine);
    }

    private void refreshScriptStatusFromAssets() {
        textScriptStatus.setText(R.string.status_script_loading);
        try {
            IccScript script = IccScriptLoader.loadFromAssets(this);
            cachedScriptStepCount = script.size();
            IccHceDebugState.setScriptLoaded(true, null);
            textScriptStatus.setText(R.string.status_script_ok);
        } catch (IOException e) {
            cachedScriptStepCount = -1;
            IccHceDebugState.setScriptLoaded(false, e.getMessage());
            textScriptStatus.setText(getString(R.string.status_script_fail, String.valueOf(e.getMessage())));
        }
    }

    private void refreshSessionStepUi() {
        int total = Math.max(IccHceDebugState.sessionTotalSteps, cachedScriptStepCount);
        if (total <= 0) {
            textSessionStep.setText(R.string.status_session_step_pending);
            return;
        }
        int next = IccHceDebugState.sessionNextStepIndex;
        textSessionStep.setText(getString(R.string.status_session_step, next, total));
    }

    private void openNfcPaymentSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_NFC_PAYMENT_SETTINGS));
        } catch (ActivityNotFoundException e) {
            openNfcSettings();
        }
    }

    private void openNfcSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        } catch (ActivityNotFoundException ignored) {
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }
    }
}
