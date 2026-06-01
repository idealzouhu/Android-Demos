package com.example.aidl.client;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aidl.common.IAidlGateway;
import com.example.aidl.common.ICalculator;
import com.example.aidl.common.IGreeter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 客户端：通过绑定服务端 GatewayService 获取 IAidlGateway，
 * 再通过网关获取 ICalculator、IGreeter 并调用（演示统一网关用法）。
 */
public class MainActivity extends AppCompatActivity {

    private static final String SERVER_PACKAGE = "com.example.aidl.server";
    private static final String GATEWAY_SERVICE_CLASS = "com.example.aidl.server.GatewayService";

    private IAidlGateway gateway;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private TextView statusText;
    private TextView logText;
    private Button btnBind;
    private Button btnUnbind;
    private Button btnCalc;
    private Button btnGreeter;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            gateway = IAidlGateway.Stub.asInterface(service);
            runOnUiThread(() -> {
                statusText.setText(R.string.status_bound);
                setBoundState(true);
                appendLog("已连接网关");
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            gateway = null;
            runOnUiThread(() -> {
                statusText.setText(R.string.status_not_bound);
                setBoundState(false);
                appendLog("已断开连接");
            });
        }
    };

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

        statusText = findViewById(R.id.status);
        logText = findViewById(R.id.log);
        btnBind = findViewById(R.id.btn_bind);
        btnUnbind = findViewById(R.id.btn_unbind);
        btnCalc = findViewById(R.id.btn_calc);
        btnGreeter = findViewById(R.id.btn_greeter);

        btnBind.setOnClickListener(v -> bindGateway());
        btnUnbind.setOnClickListener(v -> unbindGateway());
        btnCalc.setOnClickListener(v -> callCalculator());
        btnGreeter.setOnClickListener(v -> callGreeter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gateway != null) {
            unbindService(connection);
        }
        executor.shutdown();
    }

    private void bindGateway() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(SERVER_PACKAGE, GATEWAY_SERVICE_CLASS));
        try {
            boolean bound = bindService(intent, connection, BIND_AUTO_CREATE);
            if (bound) {
                appendLog("正在绑定服务…");
            } else {
                appendLog("绑定失败：无法连接到服务端");
                Toast.makeText(this, "请先安装并运行「AIDL 服务端」应用", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            appendLog("绑定失败: " + e.getMessage());
            Toast.makeText(this, "请先安装并运行「AIDL 服务端」应用", Toast.LENGTH_LONG).show();
        }
    }

    private void unbindGateway() {
        if (gateway != null) {
            unbindService(connection);
            gateway = null;
            statusText.setText(R.string.status_not_bound);
            setBoundState(false);
        }
    }

    private void setBoundState(boolean bound) {
        btnBind.setEnabled(!bound);
        btnUnbind.setEnabled(bound);
        btnCalc.setEnabled(bound);
        btnGreeter.setEnabled(bound);
    }

    private void callCalculator() {
        if (gateway == null) return;
        executor.execute(() -> {
            try {
                ICalculator calculator = gateway.getCalculator();
                int result = calculator.add(3, 5);
                runOnUiThread(() -> appendLog("ICalculator.add(3, 5) = " + result));
            } catch (RemoteException e) {
                runOnUiThread(() -> appendLog("计算器调用异常: " + e.getMessage()));
            }
        });
    }

    private void callGreeter() {
        if (gateway == null) return;
        executor.execute(() -> {
            try {
                IGreeter greeter = gateway.getGreeter();
                String msg = greeter.greet("World");
                runOnUiThread(() -> appendLog("IGreeter.greet(\"World\") = " + msg));
            } catch (RemoteException e) {
                runOnUiThread(() -> appendLog("问候调用异常: " + e.getMessage()));
            }
        });
    }

    private void appendLog(String line) {
        String old = logText.getText().toString();
        logText.setText(old.isEmpty() ? line : old + "\n" + line);
    }
}
