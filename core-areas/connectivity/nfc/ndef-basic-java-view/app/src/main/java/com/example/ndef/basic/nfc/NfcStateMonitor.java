package com.example.ndef.basic.nfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Build;

import androidx.annotation.Nullable;

/**
 * 监听 {@link NfcAdapter#ACTION_ADAPTER_STATE_CHANGED}，将适配器状态收敛为 {@link NfcState} 三态。
 */
public final class NfcStateMonitor {

    public enum NfcState {
        /** {@link NfcAdapter#getDefaultAdapter(Context)} 为 null */
        UNSUPPORTED,
        /** 硬件存在但当前关闭（含切换过程中） */
        OFF,
        ON
    }

    public interface Listener {
        void onStateChanged(NfcState state);
    }

    private final BroadcastReceiver receiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!NfcAdapter.ACTION_ADAPTER_STATE_CHANGED.equals(intent.getAction())) {
                        return;
                    }
                    if (adapter == null || listener == null) {
                        return;
                    }
                    int raw =
                            intent.getIntExtra(
                                    NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF);
                    dispatchRawState(raw);
                }
            };

    @Nullable private Context appContext;
    @Nullable private NfcAdapter adapter;
    @Nullable private Listener listener;

    public void register(Context context, Listener listener) {
        unregister();
        Context app = context.getApplicationContext();
        this.appContext = app;
        this.listener = listener;
        this.adapter = NfcAdapter.getDefaultAdapter(app);
        if (adapter == null) {
            listener.onStateChanged(NfcState.UNSUPPORTED);
            return;
        }
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            app.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            app.registerReceiver(receiver, filter);
        }
        pushCurrentAdapterState();
    }

    public void unregister() {
        if (appContext != null) {
            try {
                appContext.unregisterReceiver(receiver);
            } catch (IllegalArgumentException ignored) {
            }
        }
        appContext = null;
        adapter = null;
        listener = null;
    }

    private void pushCurrentAdapterState() {
        if (adapter == null || listener == null) {
            return;
        }
        if (adapter.isEnabled()) {
            listener.onStateChanged(NfcState.ON);
        } else {
            listener.onStateChanged(NfcState.OFF);
        }
    }

    private void dispatchRawState(int adapterState) {
        if (listener == null) {
            return;
        }
        switch (adapterState) {
            case NfcAdapter.STATE_ON:
                listener.onStateChanged(NfcState.ON);
                break;
            case NfcAdapter.STATE_OFF:
            case NfcAdapter.STATE_TURNING_OFF:
            case NfcAdapter.STATE_TURNING_ON:
            default:
                // 切换过程中仍视为不可用，避免半开状态误写入
                listener.onStateChanged(NfcState.OFF);
                break;
        }
    }
}
