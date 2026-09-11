package com.example.model

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

data class CalculationResult(
    val cementBags: Double = 0.0,
    val sandCft: Double = 0.0,
    val aggregateCft: Double = 0.0,
    val steelKgBySize: Map<Int, Double> = emptyMap(),
    val totalSteelKg: Double = 0.0,
    val bricksCount: Int = 0,
    val details: String = "",
    val volumeCft: Double = 0.0
)

data class RebarInput(
    val diaMm: Int,
    val spacingInches: Double
)

data class ColumnRebarInput(
    val diaMm: Int,
    val nos: Int
)

data class BeamBarInput(
    val sizeMm: Int,
    val qty: Int
)

object FootingCalculator {
    fun calculate(
        lengthFt: Double,
        breadthFt: Double,
        thicknessInches: Double,
        nos: Int,
        mixRatio: String = "1:1.5:3",
        rebars: List<RebarInput>
    ): CalculationResult {
        if (lengthFt <= 0 || breadthFt <= 0 || thicknessInches <= 0 || nos <= 0) {
            return CalculationResult()
        }
        val thicknessFt = thicknessInches / 12.0
        val wetVolume = lengthFt * breadthFt * thicknessFt * nos
        val dryVolume = wetVolume * 1.54

        val parts = parseMixRatio(mixRatio)
        val totalParts = parts.sum()
        val cementCft = dryVolume * (parts[0] / totalParts)
        val cementBags = ceil(cementCft / 1.25)
        val sandCft = dryVolume * (parts[1] / totalParts)
        val aggregateCft = dryVolume * (parts[2] / totalParts)

        val steelMap = mutableMapOf<Int, Double>()
        for (rebar in rebars) {
            if (rebar.diaMm <= 0 || rebar.spacingInches <= 0) continue
            val spacingFt = rebar.spacingInches / 12.0
            val numBarsLength = ceil(lengthFt / spacingFt) + 1
            val numBarsBreadth = ceil(breadthFt / spacingFt) + 1
            val totalLengthLength = numBarsLength * breadthFt * nos
            val totalLengthBreadth = numBarsBreadth * lengthFt * nos
            val totalLengthMeters = (totalLengthLength + totalLengthBreadth) * 0.3048
            val weightKg = (rebar.diaMm.toDouble().pow(2.0) / 162.0) * totalLengthMeters
            steelMap[rebar.diaMm] = (steelMap[rebar.diaMm] ?: 0.0) + weightKg
        }

        val totalSteel = steelMap.values.sum()
        val specs = "${nos}x Footing (${lengthFt}'x${breadthFt}'x${thicknessInches}\"), Mix: $mixRatio"

        return CalculationResult(
            cementBags = cementBags,
            sandCft = sandCft,
            aggregateCft = aggregateCft,
            steelKgBySize = steelMap,
            totalSteelKg = totalSteel,
            details = specs,
            volumeCft = wetVolume
        )
    }
}

object ColumnCalculator {
    fun calculate(
        isRectangular: Boolean,
        isShortColumn: Boolean,
        sizeXInches: Double,
        sizeYInches: Double,
        diaInches: Double,
        heightFt: Double,
        nos: Int,
        mixRatio: String = "1:1.5:3",
        rebars: List<ColumnRebarInput>
    ): CalculationResult {
        if (heightFt <= 0 || nos <= 0) return CalculationResult()

        val wetVolume: Double
        val stirrupLengthInches: Double

        if (isRectangular) {
            val effX = if (isShortColumn) sizeXInches + 3.0 else sizeXInches
            val effY = if (isShortColumn) sizeYInches + 3.0 else sizeYInches
            wetVolume = (effX * effY * heightFt) / 144.0 * nos
            val clearCover = 1.5
            stirrupLengthInches = 2.0 * (effX - 2 * clearCover) + 2.0 * (effY - 2 * clearCover) + 6.0
        } else {
            val effDia = if (isShortColumn) diaInches + 3.0 else diaInches
            val radius = effDia / 2.0
            wetVolume = (Math.PI * radius.pow(2.0) * heightFt) / 144.0 * nos
            val clearCover = 1.5
            val effectiveDia = effDia - 2 * clearCover
            stirrupLengthInches = Math.PI * effectiveDia + 3.0
        }

        val dryVolume = wetVolume * 1.54
        val parts = parseMixRatio(mixRatio)
        val totalParts = parts.sum()
        val cementBags = ceil((dryVolume * (parts[0] / totalParts)) / 1.25)
        val sandCft = dryVolume * (parts[1] / totalParts)
        val aggregateCft = dryVolume * (parts[2] / totalParts)

        val steelMap = mutableMapOf<Int, Double>()
        val extraHeight = if (isShortColumn) 1.5 else 0.0 // 18 inches extra for short column

        for (rebar in rebars) {
            if (rebar.diaMm <= 0 || rebar.nos <= 0) continue
            val rebarHeight = heightFt + extraHeight
            val weight = (rebar.nos * rebarHeight * rebar.diaMm.toDouble().pow(2.0) / 533.0) * nos
            steelMap[rebar.diaMm] = (steelMap[rebar.diaMm] ?: 0.0) + weight
        }

        val stirrupDia = 10
        val stirrupSpacing = if (isShortColumn) 6.0 else 5.0
        val stirrupLengthFt = stirrupLengthInches / 12.0
        val numberOfStirrups = ceil((heightFt * 12.0) / stirrupSpacing) * nos
        val stirrupWeight = (numberOfStirrups * stirrupLengthFt * stirrupDia.toDouble().pow(2.0) / 533.0)
        steelMap[stirrupDia] = (steelMap[stirrupDia] ?: 0.0) + stirrupWeight

        val typeStr = if (isRectangular) "${sizeXInches}\"x${sizeYInches}\"" else "⌀${diaInches}\""
        val catStr = if (isShortColumn) "Short" else "Long"
        val specs = "${nos}x $catStr Column ($typeStr, H:${heightFt}'), Mix: $mixRatio"

        return CalculationResult(
            cementBags = cementBags,
            sandCft = sandCft,
            aggregateCft = aggregateCft,
            steelKgBySize = steelMap,
            totalSteelKg = steelMap.values.sum(),
            details = specs,
            volumeCft = wetVolume
        )
    }
}

object BeamCalculator {
    fun calculate(
        widthInches: Double,
        depthInches: Double,
        lengthFt: Double,
        numberOfBeams: Int,
        stirrupSpacingInches: Double,
        mainBars: List<BeamBarInput>,
        extraTopNos: Int,
        extraTopDia: Int,
        extraBottomNos: Int,
        extraBottomDia: Int
    ): CalculationResult {
        if (widthInches <= 0 || depthInches <= 0 || lengthFt <= 0 || numberOfBeams <= 0) {
            return CalculationResult()
        }

        val wetVolume = (widthInches / 12.0) * (depthInches / 12.0) * lengthFt * numberOfBeams
        val dryVolume = wetVolume * 1.54
        val totalParts = 5.5 // 1:1.5:3
        val cementBags = ceil((dryVolume * (1.0 / totalParts)) / 1.25)
        val sandCft = dryVolume * (1.5 / totalParts)
        val aggregateCft = dryVolume * (3.0 / totalParts)

        val steelMap = mutableMapOf<Int, Double>()

        // Main Bars
        for (bar in mainBars) {
            if (bar.sizeMm <= 0 || bar.qty <= 0) continue
            val weight = (bar.qty * lengthFt * bar.sizeMm.toDouble().pow(2.0) / 533.0) * numberOfBeams
            steelMap[bar.sizeMm] = (steelMap[bar.sizeMm] ?: 0.0) + weight
        }

        // Extra Top Bars (per support x2, length L/3)
        if (extraTopNos > 0 && extraTopDia > 0) {
            val topLength = lengthFt / 3.0
            val topWeight = (extraTopNos * 2.0 * topLength * extraTopDia.toDouble().pow(2.0) / 533.0) * numberOfBeams
            steelMap[extraTopDia] = (steelMap[extraTopDia] ?: 0.0) + topWeight
        }

        // Extra Bottom Bars (length L/2)
        if (extraBottomNos > 0 && extraBottomDia > 0) {
            val bottomLength = lengthFt / 2.0
            val bottomWeight = (extraBottomNos * bottomLength * extraBottomDia.toDouble().pow(2.0) / 533.0) * numberOfBeams
            steelMap[extraBottomDia] = (steelMap[extraBottomDia] ?: 0.0) + bottomWeight
        }

        // 10mm Stirrups
        val widthMm = widthInches * 25.4
        val depthMm = depthInches * 25.4
        val coverMm = 1.5 * 25.4
        val stirrupLengthMeters = ((widthMm - 2 * coverMm) + (depthMm - 2 * coverMm)) * 2.0 / 1000.0
        val lengthMm = lengthFt * 304.8
        val spacingMm = (if (stirrupSpacingInches > 0) stirrupSpacingInches else 6.0) * 25.4
        val numStirrups = ceil(lengthMm / spacingMm)
        val stirrupWeightKg = numStirrups * stirrupLengthMeters * 0.617 * numberOfBeams
        steelMap[10] = (steelMap[10] ?: 0.0) + stirrupWeightKg

        val specs = "${numberOfBeams}x Beam (${widthInches}\"x${depthInches}\"x${lengthFt}'), Stirrups @${stirrupSpacingInches}\""

        return CalculationResult(
            cementBags = cementBags,
            sandCft = sandCft,
            aggregateCft = aggregateCft,
            steelKgBySize = steelMap,
            totalSteelKg = steelMap.values.sum(),
            details = specs,
            volumeCft = wetVolume
        )
    }
}

object SlabCalculator {
    fun calculate(
        lengthFt: Double,
        widthFt: Double,
        thicknessInches: Double,
        mixRatio: String = "1:1.5:3",
        mainBarSizeMm: Int,
        spacingInches: Double,
        extraBarSizeMm: Int,
        extraBarsNumSets: Int
    ): CalculationResult {
        if (lengthFt <= 0 || widthFt <= 0 || thicknessInches <= 0) return CalculationResult()

        val areaSqFt = lengthFt * widthFt
        val thicknessFt = thicknessInches / 12.0
        val volumeCft = areaSqFt * thicknessFt
        val dryVolume = volumeCft * 1.54

        val parts = parseMixRatio(mixRatio)
        val totalParts = parts.sum()
        val cementBags = ceil((dryVolume * (parts[0] / totalParts)) / 1.25)
        val sandCft = dryVolume * (parts[1] / totalParts)
        val aggregateCft = dryVolume * (parts[2] / totalParts)

        val steelMap = mutableMapOf<Int, Double>()

        // Main Bars
        if (spacingInches > 0 && mainBarSizeMm > 0) {
            val spacingFt = spacingInches / 12.0
            val numBarsL = floor(lengthFt / spacingFt) + 1
            val numBarsW = floor(widthFt / spacingFt) + 1
            val totalMainLength = (numBarsL * widthFt + numBarsW * lengthFt) * 1.05
            val mainBarWeight = (mainBarSizeMm.toDouble().pow(2.0) / 162.0) * (totalMainLength * 0.3048)
            steelMap[mainBarSizeMm] = (steelMap[mainBarSizeMm] ?: 0.0) + mainBarWeight
        }

        // Extra Top Bars
        if (extraBarsNumSets > 0 && extraBarSizeMm > 0 && spacingInches > 0) {
            val sqrtArea = sqrt(areaSqFt)
            val perimeterInches = sqrtArea * 4.0 * 12.0
            val numBars = floor(perimeterInches / (spacingInches * 2.0))
            val barLength = sqrtArea / 3.0
            val totalTopLength = numBars * barLength * extraBarsNumSets
            val topBarWeight = (extraBarSizeMm.toDouble().pow(2.0) / 162.0) * (totalTopLength * 0.3048)
            steelMap[extraBarSizeMm] = (steelMap[extraBarSizeMm] ?: 0.0) + topBarWeight
        }

        val specs = "Two-Way Slab (${lengthFt}'x${widthFt}'x${thicknessInches}\"), Area: ${"%.1f".format(areaSqFt)} sqft"

        return CalculationResult(
            cementBags = cementBags,
            sandCft = sandCft,
            aggregateCft = aggregateCft,
            steelKgBySize = steelMap,
            totalSteelKg = steelMap.values.sum(),
            details = specs,
            volumeCft = volumeCft
        )
    }
}

object StaircaseCalculator {
    fun calculate(
        lengthFeet: Double,
        lengthInches: Double,
        widthFeet: Double,
        widthInches: Double,
        thicknessInches: Double,
        risers: Int,
        treads: Int,
        riserHeightInches: Double,
        treadWidthInches: Double,
        mixRatio: String = "1:1.5:3",
        mainBarMm: Int = 12,
        mainSpacingInches: Double = 5.0,
        distBarMm: Int = 10,
        distSpacingInches: Double = 5.0
    ): CalculationResult {
        val length = lengthFeet + (lengthInches / 12.0)
        val width = widthFeet + (widthInches / 12.0)
        if (length <= 0 || width <= 0) return CalculationResult()

        val thicknessFt = thicknessInches / 12.0
        val riserFt = riserHeightInches / 12.0
        val treadFt = treadWidthInches / 12.0

        val volumeSlab = length * width * thicknessFt
        val areaTreadRiser = (0.5 * treadFt * riserFt) * width * treads
        val totalVolume = volumeSlab + areaTreadRiser
        val finalVolume = totalVolume * 1.05 // 5% wastage
        val dryVolume = finalVolume * 1.54

        val parts = parseMixRatio(mixRatio)
        val ratioSum = parts.sum()
        val cementVolume = dryVolume * (parts[0] / ratioSum)
        val cementBags = ceil(cementVolume / 1.25)
        val sandCft = dryVolume * (parts[1] / ratioSum)
        val aggregateCft = dryVolume * (parts[2] / ratioSum)

        val steelMap = mutableMapOf<Int, Double>()
        val lengthMeters = length * 0.3048
        val widthMeters = width * 0.3048

        if (mainSpacingInches > 0 && mainBarMm > 0) {
            val numMainBars = ceil(length / (mainSpacingInches / 12.0)) + 1
            val totalLengthMain = numMainBars * widthMeters * 2.0 // 2 layers
            val steelMain = (mainBarMm.toDouble().pow(2.0) / 162.0) * totalLengthMain
            steelMap[mainBarMm] = (steelMap[mainBarMm] ?: 0.0) + steelMain
        }

        if (distSpacingInches > 0 && distBarMm > 0) {
            val numDistBars = ceil(width / (distSpacingInches / 12.0)) + 1
            val totalLengthDist = numDistBars * lengthMeters * 2.0 // 2 layers
            val steelDist = (distBarMm.toDouble().pow(2.0) / 162.0) * totalLengthDist
            steelMap[distBarMm] = (steelMap[distBarMm] ?: 0.0) + steelDist
        }

        val specs = "Staircase (${"%.1f".format(length)}'x${"%.1f".format(width)}', $treads steps), Mix: $mixRatio"

        return CalculationResult(
            cementBags = cementBags,
            sandCft = sandCft,
            aggregateCft = aggregateCft,
            steelKgBySize = steelMap,
            totalSteelKg = steelMap.values.sum(),
            details = specs,
            volumeCft = totalVolume
        )
    }
}

object BrickworkCalculator {
    fun calculate(
        buildingLengthFt: Double,
        buildingWidthFt: Double,
        wallHeightFt: Double,
        lengthSpans: Int,
        widthSpans: Int,
        windowCount: Int,
        windowWidthFt: Double,
        windowHeightFt: Double,
        doorCount: Int,
        doorWidthFt: Double,
        doorHeightFt: Double,
        wallThicknessInches: Double = 10.0,
        brickLengthInches: Double = 9.5,
        brickWidthInches: Double = 4.5,
        brickHeightInches: Double = 2.75,
        plasterThicknessMm: Double = 12.0
    ): CalculationResult {
        if (buildingLengthFt <= 0 || buildingWidthFt <= 0 || wallHeightFt <= 0) return CalculationResult()

        val wallThicknessFt = wallThicknessInches / 12.0
        val brickLengthFt = (brickLengthInches + 0.5) / 12.0
        val brickWidthFt = (brickWidthInches + 0.5) / 12.0
        val brickHeightFt = (brickHeightInches + 0.5) / 12.0
        val plasterThicknessFt = plasterThicknessMm / 304.8

        // Wall Area
        val perimeterArea = 2.0 * (buildingLengthFt * wallHeightFt) + 2.0 * (buildingWidthFt * wallHeightFt)
        val internalLengthWalls = (lengthSpans.coerceAtLeast(1) - 1) * buildingWidthFt * wallHeightFt
        val internalWidthWalls = (widthSpans.coerceAtLeast(1) - 1) * buildingLengthFt * wallHeightFt
        val totalWallArea = perimeterArea + internalLengthWalls + internalWidthWalls

        // Openings
        val windowArea = windowCount * windowWidthFt * windowHeightFt
        val doorArea = doorCount * doorWidthFt * doorHeightFt
        val netWallArea = (totalWallArea - windowArea - doorArea).coerceAtLeast(0.0)

        // Brickwork
        val brickVolume = brickLengthFt * brickWidthFt * brickHeightFt
        val wallVolume = netWallArea * wallThicknessFt
        val totalBricks = if (brickVolume > 0) ceil(wallVolume / brickVolume).toInt() else 0

        val mortarVolume = wallVolume * 0.30 // 30% mortar
        val cementBrick = mortarVolume / 5.0 // 1:4 mix
        val sandBrick = cementBrick * 4.0
        val cementBrickBags = ceil(cementBrick / 1.25)

        // Plaster (Both Sides)
        val plasterArea = netWallArea * 2.0
        val plasterVolume = plasterArea * plasterThicknessFt
        val cementPlaster = plasterVolume / 5.0 // 1:4 mix
        val sandPlaster = cementPlaster * 4.0
        val cementPlasterBags = ceil(cementPlaster / 1.25)

        val totalCementBags = cementBrickBags + cementPlasterBags
        val totalSandCft = sandBrick + sandPlaster

        val specs = "Brickwork (${wallThicknessInches.toInt()}\" Wall, Net Area: ${"%.1f".format(netWallArea)} sqft), Incl. Plaster"

        return CalculationResult(
            cementBags = totalCementBags,
            sandCft = totalSandCft,
            aggregateCft = 0.0,
            bricksCount = totalBricks,
            details = specs,
            volumeCft = wallVolume
        )
    }
}

private fun parseMixRatio(mixRatio: String): List<Double> {
    return try {
        val parts = mixRatio.split(":").map { it.trim().toDouble() }
        if (parts.size == 3) parts else listOf(1.0, 1.5, 3.0)
    } catch (_: Exception) {
        listOf(1.0, 1.5, 3.0)
    }
}
