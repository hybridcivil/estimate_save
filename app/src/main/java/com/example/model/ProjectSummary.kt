package com.example.model

import com.example.data.entity.EstimateItemEntity
import com.example.data.entity.ProjectEntity

data class MaterialRow(
    val materialName: String,
    val quantity: Double,
    val unit: String,
    val unitRate: Double,
    val totalCost: Double
)

data class ProjectSummaryData(
    val project: ProjectEntity,
    val totalCementBags: Double,
    val cementCost: Double,
    val totalSandCft: Double,
    val sandCost: Double,
    val totalAggregateCft: Double,
    val aggregateCost: Double,
    val totalSteelKg: Double,
    val steelCost: Double,
    val totalBricks: Int,
    val bricksCost: Double,
    val customItemsCost: Double,
    val grandTotalCost: Double,
    val materialRows: List<MaterialRow>,
    val steelBySize: Map<Int, Double>,
    val itemsByCategory: Map<String, List<EstimateItemEntity>>
)

object ProjectSummaryCalculator {
    fun compute(project: ProjectEntity, items: List<EstimateItemEntity>): ProjectSummaryData {
        var cementBags = 0.0
        var sandCft = 0.0
        var aggregateCft = 0.0
        var steelKg = 0.0
        var bricksCount = 0
        var customCost = 0.0
        val steelMap = mutableMapOf<Int, Double>()

        val grouped = items.groupBy { it.category }

        for (item in items) {
            if (item.category == "CUSTOM") {
                customCost += item.subtotalCost
            } else {
                cementBags += item.cementBags
                sandCft += item.sandCft
                aggregateCft += item.aggregateCft
                steelKg += item.steelTotalKg
                bricksCount += item.bricksCount

                // Parse steel breakdown if available
                if (item.steelBreakdownJson.isNotBlank()) {
                    val parts = item.steelBreakdownJson.split(",")
                    for (part in parts) {
                        // format: "12mm: 45.2 kg"
                        val match = Regex("""(\d+)\s*mm\s*:\s*([\d.]+)\s*kg""").find(part.trim())
                        if (match != null) {
                            val dia = match.groupValues[1].toIntOrNull() ?: continue
                            val wt = match.groupValues[2].toDoubleOrNull() ?: continue
                            steelMap[dia] = (steelMap[dia] ?: 0.0) + wt
                        }
                    }
                }
            }
        }

        val cementCost = cementBags * project.cementRate
        val sandCost = sandCft * project.sandRate
        val aggregateCost = aggregateCft * project.aggregateRate
        val steelCost = steelKg * project.steelRate
        val bricksCost = bricksCount * project.brickRate
        val grandTotal = cementCost + sandCost + aggregateCost + steelCost + bricksCost + customCost

        val rows = mutableListOf<MaterialRow>()
        if (cementBags > 0) {
            rows.add(MaterialRow("Cement (50kg bags)", cementBags, "Bags", project.cementRate, cementCost))
        }
        if (sandCft > 0) {
            rows.add(MaterialRow("Sand (Coarse/Fine)", sandCft, "CFT", project.sandRate, sandCost))
        }
        if (aggregateCft > 0) {
            rows.add(MaterialRow("Stone Chips / Khoa", aggregateCft, "CFT", project.aggregateRate, aggregateCost))
        }
        if (steelKg > 0) {
            rows.add(MaterialRow("Rebar / MS Steel", steelKg, "KG", project.steelRate, steelCost))
        }
        if (bricksCount > 0) {
            rows.add(MaterialRow("Clay / Concrete Bricks", bricksCount.toDouble(), "Pcs", project.brickRate, bricksCost))
        }

        // Add custom items
        val customItems = grouped["CUSTOM"] ?: emptyList()
        for (c in customItems) {
            rows.add(MaterialRow(c.name, c.customQuantity, c.customUnit, c.customUnitRate, c.subtotalCost))
        }

        return ProjectSummaryData(
            project = project,
            totalCementBags = cementBags,
            cementCost = cementCost,
            totalSandCft = sandCft,
            sandCost = sandCost,
            totalAggregateCft = aggregateCft,
            aggregateCost = aggregateCost,
            totalSteelKg = steelKg,
            steelCost = steelCost,
            totalBricks = bricksCount,
            bricksCost = bricksCost,
            customItemsCost = customCost,
            grandTotalCost = grandTotal,
            materialRows = rows,
            steelBySize = steelMap,
            itemsByCategory = grouped
        )
    }
}
