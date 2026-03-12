## 四、Binder 在 Android 中的具体实现

### 4.1 什么是 AIDL

**AIDL （Android 接口定义语言）** 为开发者提供了一种便捷的方式来利用 Binder 进行进程间通信，而无需深入了解 Binder 底层的复杂实现。

开发者通过 AIDL 文件描述需要跨进程访问的接口和方法，Android SDK 工具会在构建应用时自动生成对应的 Java 接口文件。相对于直接实现 Binder 的流程，AIDL 自动生成 `Stub`和 `Proxy`类，并自动实现 `onTransact()`中的序列化/反序列化逻辑，省去了很多事情。





### 4.2 AIDL 的基本工作流程

在开始使用前，在客户端和服务端里面启动 AIDL 编译，即在 `build.gradle.kts` 添加如下配置：

```kotlin
android {

    buildFeatures {
        aidl = true
    }
}
```



#### 4.2.1 创建 AIDL 文件

此阶段的目标是**定义跨进程通信的契约**。开发者创建一个以 `.aidl`为扩展名的接口描述文件。

核心步骤为：

1. **定义接口与包名**：在项目的 `src/main/aidl/`目录下创建 `.aidl`文件。**包名必须与 Java 代码的包结构保持一致**，因为生成的代码将放置于对应包路径下。

2. **导入所需类型**：如果接口方法中使用到自定义的 `Parcelable`对象或其他 AIDL 接口，必须在此处显式导入。

3. **声明接口与方法**：在接口中定义可供远程调用的方法。**所有非基本类型的参数必须指定数据流向** (`in`, `out`, `inout`)。

   ```java
   // 文件: IUserManager.aidl
   package com.example.service;
   
   import com.example.service.User; // 自定义 Parcelable 对象
   
   interface IUserManager {
       // 返回一个用户列表
       List<User> getUserList();
       // 添加一个用户，参数从客户端流入服务端
       void addUser(in User user);
       // 注册一个回调接口，参数是另一个AIDL接口
       void registerListener(in IUserChangeListener listener);
   }
   ```

4. **定义 Parcelable 对象**：对于自定义类型，需在相同包路径(`src/main/aidl/com/example/service/`)下创建同名的 `.aidl`文件，将其声明为 `parcelable`。

   ```java
   // 文件: User.aidl
   package com.example.service;
   
   // 告知AIDL编译器：存在一个名为 User的 Java 类，并且这个类实现了 android.os.Parcelable接口。
   parcelable User;
   ```

   同时，在 Java 源代码目录（如 `src/main/java/com/example/service/`）中，有一个名为 `User`的类，并实现 `Parcelable`接口。`parcelable` 是 Android 中用于定义可跨进程（IPC）序列化对象的接口和机制。

   ```java
   // 文件: User.java (真实的Java类)
   package com.example.service;
   
   import android.os.Parcel;
   import android.os.Parcelable;
   
   public class User implements Parcelable { // 关键：实现 Parcelable
       private String name;
       private int age;
   
       // 构造方法、getter、setter 等 ...
       
       // ---------- 以下是 Parcelable 接口必须实现的部分 ----------
       protected User(Parcel in) {
           name = in.readString();
           age = in.readInt();
       }
   
       public static final Creator<User> CREATOR = new Creator<User>() {
           @Override
           public User createFromParcel(Parcel in) {
               return new User(in);
           }
   
           @Override
           public User[] newArray(int size) {
               return new User[size];
           }
       };
   
       @Override
       public int describeContents() {
           return 0;
       }
   
       @Override
       public void writeToParcel(Parcel dest, int flags) {
           dest.writeString(name);
           dest.writeInt(age);
       }
       // ------------------------------------------------------
   }
   ```

   

5. **编译生成代码**：构建项目。Android SDK 的 `aidl`工具会自动解析 `.aidl`文件，生成对应的 Java 接口类（例如 `IUserManager.java`），其中包含用于客户端通信的 `Proxy`类和服务端基类 `Stub`。



#### 4.2.2 创建服务端

此阶段的目标是**实现 AIDL 接口定义的功能，并将其暴露给客户端**。服务端通常是一个 `Service`组件。

核心步骤：

1. **实现 Stub**：创建一个继承自 AIDL 生成的 `Stub`抽象类的内部类，并实现其中定义的所有接口方法。这是**服务端业务逻辑的核心实现**。

2. **暴露 Binder 接口**：在 Service 的 `onBind()`方法中返回实现好的 `Stub`对象。

   > Binder 是底层通信机制的抽象基类/接口，而 Stub 是基于 AIDL 接口生成的、继承自 Binder的、包含特定业务方法签名的服务端辅助类。

   ```java
   public class UserService extends Service {
       private List<User> mUserList = new ArrayList<>();
       private RemoteCallbackList<IUserChangeListener> mListeners = new RemoteCallbackList<>();
   
       // 1. 实现 Stub
       private final IUserManager.Stub mBinder = new IUserManager.Stub() {
           @Override
           public List<User> getUserList() throws RemoteException {
               // 此处可执行服务端逻辑，如查询数据库
               return mUserList;
           }
   
           @Override
           public void addUser(User user) throws RemoteException {
               // 此处可执行服务端逻辑，如添加数据
               mUserList.add(user);
               // 通知所有注册的监听器
               notifyListeners(user);
           }
   
           @Override
           public void registerListener(IUserChangeListener listener) throws RemoteException {
               mListeners.register(listener);
           }
       };
   
       private void notifyListeners(User newUser) {
           // 使用 RemoteCallbackList 安全地遍历跨进程的回调接口
           int n = mListeners.beginBroadcast();
           for (int i = 0; i < n; i++) {
               try {
                   mListeners.getBroadcastItem(i).onUserAdded(newUser);
               } catch (RemoteException e) {
                   // 客户端进程可能已终止，忽略
               }
           }
           mListeners.finishBroadcast();
       }
       
       @Override
       public IBinder onBind(Intent intent) {
           // 2. 将 Binder 对象返回给客户端
           return mBinder;
       }
   }    
   ```

   

3. **配置服务**：在 `AndroidManifest.xml`中声明 Service，并可根据需要设置 `android:exported`属性以控制是否允许其他应用访问。

   ```xml
   <service
       android:name=".UserService"
       android:enabled="true"
       android:exported="true">
       <intent-filter>
           <action android:name="com.example.service.USER_SERVICE" />
       </intent-filter>
   </service>
   ```

   





#### 4.2.3 创建客户端并调用

此阶段的目标是**绑定到服务端，获取通信接口，并进行远程方法调用**。

核心步骤为：

1. **添加 `<queries>`**： 在客户端的 AndroidManifest.xml 中增加了：

   ```
   <queries>
     <package android:name="com.example.aidl.server" />
   </queries>
   ```

2. **复制 AIDL 文件**：将服务端定义的 `.aidl`文件及其相关目录结构（包括自定义 `Parcelable`的 `.aidl`文件）**原样复制**到客户端项目的相同路径下（`src/main/aidl/`）。这是确保两端拥有完全一致的接口定义和生成代码的关键。

3. **绑定服务**：在客户端（如 Activity）中，通过隐式或显式 Intent 绑定到服务端的 Service。

4. **获取 AIDL 接口**：将服务连接时返回的 `IBinder service` 对象转换成一个类型安全的、易于使用的 Java 接口对象，从而将对远程服务的调用变得和调用本地对象一样简单。

5. **进行远程调用**：通过获取到的 AIDL 接口对象，像调用本地方法一样进行调用。**所有调用必须捕获 `RemoteException`**。

```java
public class ClientActivity extends AppCompatActivity {
    private IUserManager mUserManagerService;
    private boolean mBound = false;

    // 1. 定义服务连接
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // 2. 关键转换：将IBinder转换为AIDL接口
            mUserManagerService = IUserManager.Stub.asInterface(service);
            mBound = true;
            
            // 绑定成功后即可调用服务
            fetchUserList();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 3. 发起绑定
        Intent intent = new Intent();
        intent.setAction("com.example.service.USER_SERVICE");  // 使用与服务端Manifest中一致的Action
        intent.setPackage("com.example.serverapp");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    private void fetchUserList() {
        if (!mBound || mUserManagerService == null) return;
        new Thread(() -> { 
            try {
                // 4. 发起远程调用
                List<User> list = mUserManagerService.getUserList();
                runOnUiThread(() -> updateUI(list));

                // 调用其他方法
                mUserManagerService.addUser(new User("NewUser", 25));
            } catch (RemoteException e) {
                e.printStackTrace();
                // 处理进程死亡或通信失败
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            // 4. 解绑服务
            unbindService(mConnection);
            mBound = false;
        }
    }
}
```






## 参考资料

[AIDL 语言  | Android Open Source Project](https://source.android.google.cn/docs/core/architecture/aidl/aidl-language?hl=zh-cn)