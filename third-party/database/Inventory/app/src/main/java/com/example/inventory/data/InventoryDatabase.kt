package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {
    /**
     * 声明一个返回 ItemDao 的抽象函数，以便数据库了解 DAO
     */
    abstract fun itemDao(): ItemDao

    /**
     * 定义一个 companion object，以允许访问用于创建或获取数据库的方法，并将类名称用作限定符
     */
    companion object {
        // 使用 @Volatile 确保多线程环境下该变量的可见性，任何线程修改后其他线程能立即看到最新值
        @Volatile
        private var Instance: InventoryDatabase? = null

        /**
         * 获取数据库实例的单例方法
         *
         * 采用双重检查锁定（Double-Check Locking）模式，确保多线程环境下也只有一个数据库实例。
         * 如果实例已存在，则直接返回；否则进入同步代码块创建新实例。
         *
         * @param context 用于创建数据库上下文，通常为 Application 或 Activity 的 Context
         * @return 唯一的 InventoryDatabase 实例
         */
        fun getDatabase(context: Context): InventoryDatabase {
            // 第一次检查：如果实例已存在，直接返回
            return Instance ?: synchronized(this) {
                // 第二次检查：进入同步块后再次确认实例是否存在
                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                    .build()
                    .also { Instance = it } // 保存实例到 Instance 变量
            }
        }
    }
}