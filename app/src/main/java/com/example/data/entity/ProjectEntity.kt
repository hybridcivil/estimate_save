package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val clientName: String = "",
    val location: String = "",
    val dateCreated: Long = System.currentTimeMillis(),
    val notes: String = "",
    val currencySymbol: String = "৳",
    val cementRate: Double = 550.0,      // per bag
    val sandRate: Double = 45.0,         // per cft
    val aggregateRate: Double = 120.0,   // per cft
    val steelRate: Double = 98.0,        // per kg
    val brickRate: Double = 12.0,        // per piece
    val plasterLaborRate: Double = 15.0  // per sq.ft
)
