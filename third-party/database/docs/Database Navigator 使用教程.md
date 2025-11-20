





## 问题

### 打开表格却无法加载数据

#### 问题描述

将 Android 手机上的 db 文件保存到电脑里面，使用 Database Navigator 打开。

在建立连接后，查看数据库里面的某张表格的数据，却报错

```
Could not load data for table "main.student". Error detais: [$QllTE ERROR] SOL error or missing database (incomplete input)
```



![image-20251120110858973](images/image-20251120110858973.png)





### 原因分析

使用了过滤器，导致没有数据能通过过滤器

![image-20251120110932525](images/image-20251120110932525.png)



#### 解决方案

不使用 filter 即可。