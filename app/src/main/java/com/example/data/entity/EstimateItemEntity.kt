package com.example.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "estimate_items",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class EstimateItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val category: String, // FOOTING, COLUMN, BEAM, SLAB, STAIRCASE, BRICKWORK, CUSTOM
    val name: String,
    val specsDescription: String = "",
    val cementBags: Double = 0.0,
    val sandCft: Double = 0.0,
    val aggregateCft: Double = 0.0,
    val steelTotalKg: Double = 0.0,
    val steelBreakdownJson: String = "", // e.g. "10mm: 35.5 kg, 12mm: 120.0 kg"
    val bricksCount: Int = 0,
    val customQuantity: Double = 0.0,
    val customUnit: String = "",
    val customUnitRate: Double = 0.0,
    val subtotalCost: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
