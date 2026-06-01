package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO 接口
 *
 * 数据库操作的执行可能用时较长，因此需要在单独的线程上运行。Room 不允许在主线程上访问数据库。
 * 所以我们需要使用 suspend 修饰符来告诉 Room 运行数据库操作。
 */
@Dao
interface ItemDao {
    /**
     * 参数 onConflict 用于告知 Room 在发生冲突时应该执行的操作。
     * 由于我们仅从一处（即 Add Item 界面）插入实体，因此我们预计不会发生任何冲突，可以将冲
     * 突策略设为 Ignore
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    /**
     * 更新的实体与传入的实体具有相同的主键。您可以更新该实体的部分或全部其他属性。
     */
    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    /**
     * 自定义查询
     *
     * 建议在持久性层中使用 Flow。将返回值类型设为 Flow 后，只要数据库中的数据发生更改，您就会收到通知。
     * Room 会为您保持更新此 Flow，也就是说，您只需要显式获取一次数据。
     *
     * 由于返回值类型为 Flow，Room 还会在后台线程上运行该查询。您无需将其明确设为 suspend 函数并在协
     * 程作用域内调用它。
     *
     * 注意，:id 在查询中使用英文冒号来引用函数中的参数。
     *
     */
    @Query("SELECT * from items WHERE id = :id")
    fun getItem(id: Int): Flow<Item>

    @Query("SELECT * from items ORDER BY name ASC")
    fun getAllItems(): Flow<List<Item>>
}