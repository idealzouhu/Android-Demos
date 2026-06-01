

## 一、onSaveInstanceState 概述

### 1.1 什么是 onSaveInstanceState 

onSaveInstanceState() 是 Android 中用于**临时保存 Activity/Fragment 状态**的生命周期方法，主要用于应对**配置变更**和**系统杀死进程**的场景。

基本案例如下：

```java
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int counter = 0;
    private String inputText = "";
    private boolean checkboxState = false;
    
    private TextView textView;
    private EditText editText;
    private CheckBox checkBox;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化UI组件
        textView = findViewById(R.id.textView);
        editText = findViewById(R.id.editText);
        checkBox = findViewById(R.id.checkBox);
        
        // 恢复之前保存的状态
        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt("counter", 0);
            inputText = savedInstanceState.getString("input_text", "");
            checkboxState = savedInstanceState.getBoolean("checkbox", false);
            
            // 恢复 UI
            textView.setText("Count: " + counter);
            editText.setText(inputText);
            checkBox.setChecked(checkboxState);
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前状态
        outState.putInt("counter", counter);
        outState.putString("input_text", editText.getText().toString());
        outState.putBoolean("checkbox", checkBox.isChecked());
    }
}
```



### 1.2 onSaveInstanceState 和 ViewModel 的区别

简单、独立的 UI 状态，使用 rememberSaveable。复杂业务逻辑、共享数据则使用 ViewModel。

| 特性             | onSaveInstanceState            | ViewModel                        |
| ---------------- | ------------------------------ | -------------------------------- |
| **主要目的**     | 保存和恢复临时 UI 状态         | 管理 UI 相关数据，处理配置变更   |
| **数据生命周期** | 仅在配置变更和进程被杀时保存   | Activity/Fragment 的整个生命周期 |
| **存储位置**     | Bundle（系统管理）             | 内存中                           |
| **数据持久性**   | 临时（可跨进程杀死）           | 临时（配置变更期间）             |
| **使用场景**     | 输入框内容、滚动位置、选择状态 | 列表数据、用户信息、网络数据     |
| **数据大小**     | 有限制（通常 1MB）             | 无硬性限制（但需考虑内存）       |







## 二、工作原理

### 2.1 数据存储流程



onSaveInstanceState 主要存在于系统管理的 Bundle。



```
Activity/Fragment 状态 → Bundle → 系统进程 → 系统恢复
       ↓                     ↓           ↓
   触发保存            序列化存储     系统管理
   时机：              位置：        恢复时机：
   - 配置变更前       ActivityRecord    Activity
   - 进入后台前                       重新创建时
```



### 2.2 生命周期时序

```
正常流程：
onCreate() → onStart() → onResume() → 用户交互 → onPause() → onStop() → onDestroy()

配置变更流程：
onPause() → onSaveInstanceState() → onStop() → onDestroy() → onCreate(savedInstanceState) → onStart() → onRestoreInstanceState() → onResume()

进程被杀死恢复：
onSaveInstanceState() → 进程被杀死 → onCreate(savedInstanceState) → onRestoreInstanceState()
```







## 最佳实践

- Bundle 通常有 **1MB** 的大小限制，只保留必要的简单数据。

