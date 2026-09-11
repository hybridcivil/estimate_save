package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.EstimateItemEntity
import com.example.data.entity.ProjectEntity
import com.example.data.repository.EstimateRepository
import com.example.model.ProjectSummaryCalculator
import com.example.model.ProjectSummaryData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EstimateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EstimateRepository
    private val prefs = application.getSharedPreferences("building_estimate_prefs", Context.MODE_PRIVATE)

    // User Session
    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString("user_name", "Engr. Kali") ?: "Engr. Kali")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userRole = MutableStateFlow(prefs.getString("user_role", "Civil Engineer") ?: "Civil Engineer")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    // Projects list
    val allProjects: StateFlow<List<ProjectEntity>>

    // Selected Active Project
    private val _activeProject = MutableStateFlow<ProjectEntity?>(null)
    val activeProject: StateFlow<ProjectEntity?> = _activeProject.asStateFlow()

    // Items for current active project
    private val _activeProjectItems = MutableStateFlow<List<EstimateItemEntity>>(emptyList())
    val activeProjectItems: StateFlow<List<EstimateItemEntity>> = _activeProjectItems.asStateFlow()

    // Summary of current active project
    val projectSummary: StateFlow<ProjectSummaryData?>

    // UI event messages
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = EstimateRepository(db.projectDao(), db.estimateItemDao())

        allProjects = repository.allProjects
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        projectSummary = combine(_activeProject, _activeProjectItems) { proj, items ->
            if (proj != null) {
                ProjectSummaryCalculator.compute(proj, items)
            } else null
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        // Seed initial project if empty
        viewModelScope.launch {
            allProjects.collectLatest { list ->
                if (list.isEmpty()) {
                    val defaultProj = ProjectEntity(
                        title = "Sample Building Project (G+3)",
                        clientName = "Bashundhara R/A Residence",
                        location = "Block D, Road 7, Dhaka",
                        notes = "Initial estimated BOQ for 4-storied residential building",
                        currencySymbol = "৳",
                        cementRate = 560.0,
                        sandRate = 48.0,
                        aggregateRate = 125.0,
                        steelRate = 98.0,
                        brickRate = 12.5,
                        plasterLaborRate = 18.0
                    )
                    val id = repository.insertProject(defaultProj)
                    _activeProject.value = defaultProj.copy(id = id)
                } else if (_activeProject.value == null) {
                    _activeProject.value = list.first()
                }
            }
        }

        // Listen to active project item updates
        viewModelScope.launch {
            _activeProject.collectLatest { proj ->
                if (proj != null) {
                    repository.getItemsForProject(proj.id).collectLatest { items ->
                        _activeProjectItems.value = items
                    }
                } else {
                    _activeProjectItems.value = emptyList()
                }
            }
        }
    }

    fun login(name: String, role: String, rememberMe: Boolean = true) {
        val finalName = if (name.isNotBlank()) name.trim() else "Engr. Kali"
        val finalRole = if (role.isNotBlank()) role.trim() else "Civil Engineer"
        _userName.value = finalName
        _userRole.value = finalRole
        _isLoggedIn.value = true

        if (rememberMe) {
            prefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("user_name", finalName)
                .putString("user_role", finalRole)
                .apply()
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }

    fun selectProject(project: ProjectEntity) {
        _activeProject.value = project
    }

    fun createProject(
        title: String,
        client: String,
        location: String,
        notes: String,
        currency: String = "৳",
        cementRate: Double = 560.0,
        sandRate: Double = 48.0,
        aggregateRate: Double = 125.0,
        steelRate: Double = 98.0,
        brickRate: Double = 12.5
    ) {
        viewModelScope.launch {
            val newProject = ProjectEntity(
                title = title.ifBlank { "New Project" },
                clientName = client,
                location = location,
                notes = notes,
                currencySymbol = currency,
                cementRate = cementRate,
                sandRate = sandRate,
                aggregateRate = aggregateRate,
                steelRate = steelRate,
                brickRate = brickRate
            )
            val newId = repository.insertProject(newProject)
            _activeProject.value = newProject.copy(id = newId)
            _userMessage.value = "Project '$title' created successfully!"
        }
    }

    fun updateProjectRates(
        cementRate: Double,
        sandRate: Double,
        aggregateRate: Double,
        steelRate: Double,
        brickRate: Double,
        currencySymbol: String
    ) {
        val current = _activeProject.value ?: return
        viewModelScope.launch {
            val updated = current.copy(
                cementRate = cementRate,
                sandRate = sandRate,
                aggregateRate = aggregateRate,
                steelRate = steelRate,
                brickRate = brickRate,
                currencySymbol = currencySymbol
            )
            repository.updateProject(updated)
            _activeProject.value = updated
            _userMessage.value = "Material rates updated!"
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
            if (_activeProject.value?.id == project.id) {
                val remaining = allProjects.value.filter { it.id != project.id }
                _activeProject.value = remaining.firstOrNull()
            }
            _userMessage.value = "Project deleted."
        }
    }

    fun saveEstimateItem(
        category: String,
        name: String,
        specs: String,
        cementBags: Double = 0.0,
        sandCft: Double = 0.0,
        aggregateCft: Double = 0.0,
        steelTotalKg: Double = 0.0,
        steelBreakdown: String = "",
        bricksCount: Int = 0,
        customQty: Double = 0.0,
        customUnit: String = "",
        customRate: Double = 0.0
    ) {
        val currentProj = _activeProject.value ?: return

        // Compute subtotal cost for this item
        val subtotal = (cementBags * currentProj.cementRate) +
                (sandCft * currentProj.sandRate) +
                (aggregateCft * currentProj.aggregateRate) +
                (steelTotalKg * currentProj.steelRate) +
                (bricksCount * currentProj.brickRate) +
                (customQty * customRate)

        val item = EstimateItemEntity(
            projectId = currentProj.id,
            category = category,
            name = name,
            specsDescription = specs,
            cementBags = cementBags,
            sandCft = sandCft,
            aggregateCft = aggregateCft,
            steelTotalKg = steelTotalKg,
            steelBreakdownJson = steelBreakdown,
            bricksCount = bricksCount,
            customQuantity = customQty,
            customUnit = customUnit,
            customUnitRate = customRate,
            subtotalCost = subtotal
        )

        viewModelScope.launch {
            repository.insertItem(item)
            _userMessage.value = "Saved '$name' to ${currentProj.title}!"
        }
    }

    fun deleteEstimateItem(item: EstimateItemEntity) {
        viewModelScope.launch {
            repository.deleteItem(item)
            _userMessage.value = "Item removed from estimate."
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
