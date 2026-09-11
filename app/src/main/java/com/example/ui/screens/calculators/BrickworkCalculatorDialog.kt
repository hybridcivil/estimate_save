package com.example.ui.screens.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.BrickworkCalculator
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@Composable
fun BrickworkCalculatorDialog(
    onDismiss: () -> Unit,
    onSaveToProject: (
        name: String,
        specs: String,
        cementBags: Double,
        sandCft: Double,
        bricksCount: Int
    ) -> Unit
) {
    var name by remember { mutableStateOf("Brickwork & Plaster") }

    var buildingLengthFt by remember { mutableStateOf("40.0") }
    var buildingWidthFt by remember { mutableStateOf("30.0") }
    var wallHeightFt by remember { mutableStateOf("10.0") }

    var lengthSpans by remember { mutableStateOf("3") }
    var widthSpans by remember { mutableStateOf("2") }

    var windowCount by remember { mutableStateOf("6") }
    var windowWidthFt by remember { mutableStateOf("4.0") }
    var windowHeightFt by remember { mutableStateOf("4.5") }

    var doorCount by remember { mutableStateOf("3") }
    var doorWidthFt by remember { mutableStateOf("3.25") }
    var doorHeightFt by remember { mutableStateOf("7.0") }

    var is10InchWall by remember { mutableStateOf(true) }

    var brickLengthInches by remember { mutableStateOf("9.5") }
    var brickWidthInches by remember { mutableStateOf("4.5") }
    var brickHeightInches by remember { mutableStateOf("2.75") }

    var plasterThicknessMm by remember { mutableStateOf("12.0") }

    val calculationResult by remember(
        name, buildingLengthFt, buildingWidthFt, wallHeightFt,
        lengthSpans, widthSpans, windowCount, windowWidthFt, windowHeightFt,
        doorCount, doorWidthFt, doorHeightFt, is10InchWall,
        brickLengthInches, brickWidthInches, brickHeightInches, plasterThicknessMm
    ) {
        val l = buildingLengthFt.toDoubleOrNull() ?: 0.0
        val w = buildingWidthFt.toDoubleOrNull() ?: 0.0
        val h = wallHeightFt.toDoubleOrNull() ?: 0.0
        val lSpans = lengthSpans.toIntOrNull() ?: 1
        val wSpans = widthSpans.toIntOrNull() ?: 1
        val wc = windowCount.toIntOrNull() ?: 0
        val ww = windowWidthFt.toDoubleOrNull() ?: 0.0
        val wh = windowHeightFt.toDoubleOrNull() ?: 0.0
        val dc = doorCount.toIntOrNull() ?: 0
        val dw = doorWidthFt.toDoubleOrNull() ?: 0.0
        val dh = doorHeightFt.toDoubleOrNull() ?: 0.0
        val wallThick = if (is10InchWall) 10.0 else 5.0
        val bl = brickLengthInches.toDoubleOrNull() ?: 9.5
        val bw = brickWidthInches.toDoubleOrNull() ?: 4.5
        val bh = brickHeightInches.toDoubleOrNull() ?: 2.75
        val pt = plasterThicknessMm.toDoubleOrNull() ?: 12.0

        mutableStateOf(
            BrickworkCalculator.calculate(
                buildingLengthFt = l,
                buildingWidthFt = w,
                wallHeightFt = h,
                lengthSpans = lSpans,
                widthSpans = wSpans,
                windowCount = wc,
                windowWidthFt = ww,
                windowHeightFt = wh,
                doorCount = dc,
                doorWidthFt = dw,
                doorHeightFt = dh,
                wallThicknessInches = wallThick,
                brickLengthInches = bl,
                brickWidthInches = bw,
                brickHeightInches = bh,
                plasterThicknessMm = pt
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(color = NavyPrimary, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Brickwork & Plaster Calculator",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bricks Count, Mortar & 2-Sided Plaster",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Wall Identifier / Section") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Building Dimensions", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = buildingLengthFt,
                            onValueChange = { buildingLengthFt = it },
                            label = { Text("Length (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = buildingWidthFt,
                            onValueChange = { buildingWidthFt = it },
                            label = { Text("Width (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = wallHeightFt,
                            onValueChange = { wallHeightFt = it },
                            label = { Text("Height (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Room Spans & Partitions", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = lengthSpans,
                            onValueChange = { lengthSpans = it },
                            label = { Text("Spans along Length") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = widthSpans,
                            onValueChange = { widthSpans = it },
                            label = { Text("Spans along Width") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Wall Thickness Selector
                    Text("Wall Thickness", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = is10InchWall,
                            onClick = { is10InchWall = true },
                            label = { Text("10 inch (Exterior / Load Bearing)") }
                        )
                        FilterChip(
                            selected = !is10InchWall,
                            onClick = { is10InchWall = false },
                            label = { Text("5 inch (Partition Wall)") }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Openings (Deductions)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = windowCount,
                            onValueChange = { windowCount = it },
                            label = { Text("Windows Nos") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = windowWidthFt,
                            onValueChange = { windowWidthFt = it },
                            label = { Text("W Width (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = windowHeightFt,
                            onValueChange = { windowHeightFt = it },
                            label = { Text("W Height (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = doorCount,
                            onValueChange = { doorCount = it },
                            label = { Text("Doors Nos") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = doorWidthFt,
                            onValueChange = { doorWidthFt = it },
                            label = { Text("D Width (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = doorHeightFt,
                            onValueChange = { doorHeightFt = it },
                            label = { Text("D Height (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Output Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Estimated Material Output",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NavyPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Bricks Required:", fontSize = 13.sp, color = Color(0xFF475569))
                                Text("${"%,d".format(calculationResult.bricksCount)} pcs", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = GoldAccent)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Cement (Brick + Plaster):", fontSize = 13.sp, color = Color(0xFF475569))
                                Text("${calculationResult.cementBags.toInt()} bags (50kg)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Sand (Brick + Plaster):", fontSize = 13.sp, color = Color(0xFF475569))
                                Text("${"%.1f".format(calculationResult.sandCft)} CFT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 4.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                onSaveToProject(
                                    name.ifBlank { "Brickwork & Plaster" },
                                    calculationResult.details,
                                    calculationResult.cementBags,
                                    calculationResult.sandCft,
                                    calculationResult.bricksCount
                                )
                                onDismiss()
                            },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save to Project", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
