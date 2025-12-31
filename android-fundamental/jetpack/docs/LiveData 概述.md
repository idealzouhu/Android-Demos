### 什么是 LiveData

在 Activity 中，我们手动获取 ViewModel 中的数据，但是 ViewModel 却无法将数据的变化主动通知给 Activity。如果将 Activity 的实例传给 ViewModel ，这样 ViewModel 却无法将数据的变化主动通知给 Activity。但是，ViewModel 的生命周期长于 Activity，这样会导致Activity 无法释放从而造成内存泄漏。

为了解决这一问题，Android 官方提供了 LiveData 这一解决方案。LiveData 是 Jetpack 提供的一种响应式编程组件,它可以包含任何类型的数据,并在数据发生变化的时候通知给观察者。其特点主要有:

- **数据持有与观察**:  LiveData 可以包含任何类型的数据，当数据发生变化时，它会自动通知所有处于活跃状态的观察者。观察者可以是 Activity、Fragment 或其他组件。
- **避免内存泄漏**：LiveData 会自动感知生命周期，当观察者（如 Activity）销毁时会自动移除观察，无需手动清理
- **生命周期安全**：只在观察者处于活跃状态（如 Activity 的 onStart 到 onStop 之间）才发送数据更新，避免不必要的 UI 刷新



### LiveData 的基本用法

LiveData特别适合与ViewModel结合在一起使用，虽然它也可以单独用在别的地方，但是在绝大多数情况下，它是使用在ViewModel当中的。

```java
// 在 ViewModel 中定义 LiveData
public class MyViewModel extends ViewModel {
    private MutableLiveData<String> _data = new MutableLiveData<>();
    private LiveData<String> data = _data;
    
    public LiveData<String> getData() {
        return data;
    }
    
    public void updateData(String newData) {
        _data.setValue(newData);
    }
}


// 在 Activity 中观察
public class MyActivity extends AppCompatActivity {
    private MyViewModel viewModel;
    private TextView textView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textView = findViewById(R.id.textView);
        
        // 获取 ViewModel
        viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        
        // 观察 LiveData
        viewModel.getData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String data) {
                // 更新 UI
                textView.setText(data);
            }
        });
    }
}

```

其中，View 只能观察，不能直接修改数据。这个访问控制细节如下：

- _data：`MutableLiveData<String>`类型，是可变的（有 setValue()和 postValue()方法）
- data：`LiveData<String>`类型，是只读的（只有观察功能，没有修改功能）

