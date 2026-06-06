[`ViewModel`](https://developer.android.google.cn/topic/libraries/architecture/viewmodel?hl=zh-cn) 是 Android Jetpack 库中的架构组件之一，可用于存储应用数据。当框架在配置更改或其他事件期间销毁并重新创建 activity 时，存储的数据不会丢失。不过，如果 activity 因进程终止而被销毁，数据将会丢失。`ViewModel` 只能通过快速重新创建 activity 缓存数据。







### 为什么不会丢失数据

Activity 和 Fragment 在屏幕旋转时都不会丢失 ViewModel，因为 ViewModel 并不保存在它们自身，而是保存在 Activity 的 `ViewModelStore`中。

为了保存在 Activity 的 `ViewModelStore`，使用 `new ViewModelProvider(this).get(MainViewModel.class);` 来实例化。

```kotlin
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private TextView tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCount = findViewById(R.id.tv_count);

        // 获取 ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 观察数据变化
        viewModel.getCount().observe(this, count -> {
            tvCount.setText(String.valueOf(count));
        });
    }
}
```

