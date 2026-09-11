package com.example.data.repository

import com.example.data.dao.EstimateItemDao
import com.example.data.dao.ProjectDao
import com.example.data.entity.EstimateItemEntity
import com.example.data.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

class EstimateRepository(
    private val projectDao: ProjectDao,
    private val estimateItemDao: EstimateItemDao
) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun getProject(id: Long): Flow<ProjectEntity?> = projectDao.getProjectById(id)

    suspend fun getProjectDirect(id: Long): ProjectEntity? = projectDao.getProjectByIdDirect(id)

    suspend fun insertProject(project: ProjectEntity): Long = projectDao.insertProject(project)

    suspend fun updateProject(project: ProjectEntity) = projectDao.updateProject(project)

    suspend fun deleteProject(project: ProjectEntity) = projectDao.deleteProject(project)

    suspend fun deleteProjectById(id: Long) = projectDao.deleteProjectById(id)

    fun getItemsForProject(projectId: Long): Flow<List<EstimateItemEntity>> =
        estimateItemDao.getItemsForProject(projectId)

    suspend fun getItemsForProjectDirect(projectId: Long): List<EstimateItemEntity> =
        estimateItemDao.getItemsForProjectDirect(projectId)

    suspend fun insertItem(item: EstimateItemEntity): Long = estimateItemDao.insertItem(item)

    suspend fun updateItem(item: EstimateItemEntity) = estimateItemDao.updateItem(item)

    suspend fun deleteItem(item: EstimateItemEntity) = estimateItemDao.deleteItem(item)

    suspend fun deleteItemById(id: Long) = estimateItemDao.deleteItemById(id)
}
