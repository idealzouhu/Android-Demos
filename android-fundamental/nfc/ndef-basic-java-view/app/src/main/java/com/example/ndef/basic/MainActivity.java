package com.example.ndef.basic;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ndef.basic.nfc.NdefPayloads;
import com.example.ndef.basic.nfc.NdefReader;
import com.example.ndef.basic.nfc.NdefWriter;
import com.example.ndef.basic.nfc.NfcStateMonitor;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private enum Mode {
        READ,
        WRITE
    }

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] techLists;

    private final NfcStateMonitor nfcMonitor = new NfcStateMonitor();

    private Mode mode = Mode.READ;
    private NdefMessage pendingWrite;

    private TextView statusText;
    private MaterialButton openNfcSettings;
    private MaterialButtonToggleGroup modeGroup;
    private MaterialRadioButton writeTypeText;
    private MaterialRadioButton writeTypeUri;
    private TextInputEditText writeContent;
    private MaterialButton prepareWrite;
    private TextView writeResult;
    private View readPanel;
    private View writePanel;
    private TextView readResult;
    private MaterialButton clearRead;

    private final NfcStateMonitor.Listener nfcStateListener =
            state -> runOnUiThread(() -> applyNfcState(state));

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

        bindViews();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        int piFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            piFlags |= PendingIntent.FLAG_MUTABLE;
        }
        Intent piIntent =
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, piIntent, piFlags);

        IntentFilter ndef =
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new IllegalStateException(e);
        }
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFilters = new IntentFilter[] {ndef, tech, tag};
        techLists =
                new String[][] {
                    new String[] {Ndef.class.getName()},
                    new String[] {NdefFormatable.class.getName()},
                    new String[] {"android.nfc.tech.MifareUltralight"}
                };

        modeGroup.addOnButtonCheckedListener(
                (group, checkedId, isChecked) -> {
                    if (!isChecked) {
                        return;
                    }
                    if (checkedId == R.id.modeRead) {
                        mode = Mode.READ;
                    } else if (checkedId == R.id.modeWrite) {
                        mode = Mode.WRITE;
                    }
                    pendingWrite = null;
                    updatePanels();
                });

        openNfcSettings.setOnClickListener(
                v -> startActivity(new Intent(Settings.ACTION_NFC_SETTINGS)));

        clearRead.setOnClickListener(
                v -> readResult.setText(getString(R.string.read_result_empty)));

        prepareWrite.setOnClickListener(v -> onPrepareWriteClicked());

        updatePanels();
        applyNfcState(initialNfcState());

        if (isNfcDeliveryIntent(getIntent())) {
            dispatchNfcIntent(getIntent());
        }
    }

    private void bindViews() {
        statusText = findViewById(R.id.statusText);
        openNfcSettings = findViewById(R.id.openNfcSettings);
        modeGroup = findViewById(R.id.modeGroup);
        writeTypeText = findViewById(R.id.writeTypeText);
        writeTypeUri = findViewById(R.id.writeTypeUri);
        writeContent = findViewById(R.id.writeContent);
        prepareWrite = findViewById(R.id.prepareWrite);
        writeResult = findViewById(R.id.writeResult);
        readPanel = findViewById(R.id.readPanel);
        writePanel = findViewById(R.id.writePanel);
        readResult = findViewById(R.id.readResult);
        clearRead = findViewById(R.id.clearRead);
    }

    private NfcStateMonitor.NfcState initialNfcState() {
        if (nfcAdapter == null) {
            return NfcStateMonitor.NfcState.UNSUPPORTED;
        }
        return nfcAdapter.isEnabled()
                ? NfcStateMonitor.NfcState.ON
                : NfcStateMonitor.NfcState.OFF;
    }

    private void applyNfcState(NfcStateMonitor.NfcState state) {
        switch (state) {
            case UNSUPPORTED:
                statusText.setText(R.string.nfc_status_unsupported);
                openNfcSettings.setVisibility(View.GONE);
                break;
            case OFF:
                statusText.setText(R.string.nfc_status_off);
                openNfcSettings.setVisibility(View.VISIBLE);
                break;
            case ON:
                statusText.setText(R.string.nfc_status_on);
                openNfcSettings.setVisibility(View.GONE);
                break;
        }
        boolean enabled = state == NfcStateMonitor.NfcState.ON;
        modeGroup.setEnabled(enabled);
        clearRead.setEnabled(enabled);
        prepareWrite.setEnabled(enabled);
        writeContent.setEnabled(enabled);
        writeTypeText.setEnabled(enabled);
        writeTypeUri.setEnabled(enabled);
    }

    private void updatePanels() {
        boolean read = mode == Mode.READ;
        readPanel.setVisibility(read ? View.VISIBLE : View.GONE);
        writePanel.setVisibility(read ? View.GONE : View.VISIBLE);
    }

    private void onPrepareWriteClicked() {
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            writeResult.setText(getString(R.string.nfc_disabled_hint));
            return;
        }
        CharSequence cs = writeContent.getText();
        String content = cs != null ? cs.toString().trim() : "";
        if (content.isEmpty()) {
            writeResult.setText(getString(R.string.error_write_empty));
            return;
        }
        if (writeTypeUri.isChecked()) {
            Uri uri = Uri.parse(content);
            if (uri.getScheme() == null || uri.getScheme().isEmpty()) {
                writeResult.setText(getString(R.string.error_write_uri_invalid));
                return;
            }
            pendingWrite =
                    new NdefMessage(
                            new NdefRecord[] {NdefPayloads.uriRecord(content)});
        } else {
            pendingWrite =
                    new NdefMessage(
                            new NdefRecord[] {
                                NdefPayloads.textRecord(Locale.getDefault(), content)
                            });
        }
        writeResult.setText(getString(R.string.write_waiting));
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcMonitor.register(this, nfcStateListener);
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        nfcMonitor.unregister();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        dispatchNfcIntent(intent);
    }

    private static boolean isNfcDeliveryIntent(Intent intent) {
        if (intent == null) {
            return false;
        }
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            return true;
        }
        return intent.hasExtra(NfcAdapter.EXTRA_TAG)
                || intent.hasExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
    }

    private void dispatchNfcIntent(Intent intent) {
        if (!isNfcDeliveryIntent(intent)) {
            return;
        }
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            return;
        }
        if (mode == Mode.READ) {
            NdefReader.ParsedResult result = NdefReader.parse(intent);
            readResult.setText(result.displayText);
            return;
        }
        if (pendingWrite == null) {
            return;
        }
        Tag tag = NdefReader.extractTag(intent);
        if (tag == null) {
            return;
        }
        NdefWriter.WriteResult wr = NdefWriter.write(tag, pendingWrite);
        writeResult.setText(formatWriteResult(wr));
        if (wr.status == NdefWriter.Status.OK) {
            pendingWrite = null;
        }
    }

    private String formatWriteResult(NdefWriter.WriteResult wr) {
        switch (wr.status) {
            case OK:
                return getString(R.string.write_ok);
            case NOT_WRITABLE:
                return getString(R.string.write_not_writable);
            case SIZE_EXCEEDED:
                return getString(R.string.write_size_exceeded);
            case IO_ERROR:
                return getString(
                        R.string.write_io_error, wr.detail != null ? wr.detail : "");
            case FORMAT_ERROR:
                return getString(
                        R.string.write_format_error, wr.detail != null ? wr.detail : "");
            default:
                return getString(R.string.write_not_writable);
        }
    }
}
