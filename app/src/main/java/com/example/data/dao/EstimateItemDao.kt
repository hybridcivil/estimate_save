package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.EstimateItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimateItemDao {
    @Query("SELECT * FROM estimate_items WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getItemsForProject(projectId: Long): Flow<List<EstimateItemEntity>>

    @Query("SELECT * FROM estimate_items WHERE projectId = :projectId ORDER BY timestamp DESC")
    suspend fun getItemsForProjectDirect(projectId: Long): List<EstimateItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: EstimateItemEntity): Long

    @Update
    suspend fun updateItem(item: EstimateItemEntity)

    @Delete
    suspend fun deleteItem(item: EstimateItemEntity)

    @Query("DELETE FROM estimate_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM estimate_items WHERE projectId = :projectId")
    suspend fun deleteItemsForProject(projectId: Long)
}
