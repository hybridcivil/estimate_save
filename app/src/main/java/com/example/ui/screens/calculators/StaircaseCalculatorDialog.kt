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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
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
import com.example.model.StaircaseCalculator
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaircaseCalculatorDialog(
    onDismiss: () -> Unit,
    onSaveToProject: (
        name: String,
        specs: String,
        cementBags: Double,
        sandCft: Double,
        aggregateCft: Double,
        steelTotalKg: Double,
        steelBreakdown: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("Main Staircase") }
    var mixRatio by remember { mutableStateOf("1:1.5:3") }
    var mixDropdownExpanded by remember { mutableStateOf(false) }

    var lengthFeet by remember { mutableStateOf("10") }
    var lengthInches by remember { mutableStateOf("0") }
    var widthFeet by remember { mutableStateOf("3") }
    var widthInches by remember { mutableStateOf("6") }
    var thicknessInches by remember { mutableStateOf("6") }

    var risers by remember { mutableStateOf("10") }
    var treads by remember { mutableStateOf("9") }
    var riserHeightInches by remember { mutableStateOf("6") }
    var treadWidthInches by remember { mutableStateOf("10") }

    var mainBarMm by remember { mutableStateOf("12") }
    var mainSpacingInches by remember { mutableStateOf("5.0") }
    var distBarMm by remember { mutableStateOf("10") }
    var distSpacingInches by remember { mutableStateOf("5.0") }

    val calculationResult by remember(
        name, mixRatio, lengthFeet, lengthInches, widthFeet, widthInches, thicknessInches,
        risers, treads, riserHeightInches, treadWidthInches,
        mainBarMm, mainSpacingInches, distBarMm, distSpacingInches
    ) {
        val lf = lengthFeet.toDoubleOrNull() ?: 0.0
        val li = lengthInches.toDoubleOrNull() ?: 0.0
        val wf = widthFeet.toDoubleOrNull() ?: 0.0
        val wi = widthInches.toDoubleOrNull() ?: 0.0
        val th = thicknessInches.toDoubleOrNull() ?: 0.0
        val r = risers.toIntOrNull() ?: 0
        val tr = treads.toIntOrNull() ?: 0
        val rh = riserHeightInches.toDoubleOrNull() ?: 0.0
        val tw = treadWidthInches.toDoubleOrNull() ?: 0.0
        val mb = mainBarMm.toIntOrNull() ?: 12
        val ms = mainSpacingInches.toDoubleOrNull() ?: 5.0
        val db = distBarMm.toIntOrNull() ?: 10
        val ds = distSpacingInches.toDoubleOrNull() ?: 5.0

        mutableStateOf(
            StaircaseCalculator.calculate(
                lengthFeet = lf,
                lengthInches = li,
                widthFeet = wf,
                widthInches = wi,
                thicknessInches = th,
                risers = r,
                treads = tr,
                riserHeightInches = rh,
                treadWidthInches = tw,
                mixRatio = mixRatio,
                mainBarMm = mb,
                mainSpacingInches = ms,
                distBarMm = db,
                distSpacingInches = ds
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
                                text = "Staircase Material Calculator",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Slab, Tread/Riser Steps & 2-Layer Rebars",
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Staircase Identifier") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        ExposedDropdownMenuBox(
                            expanded = mixDropdownExpanded,
                            onExpandedChange = { mixDropdownExpanded = !mixDropdownExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = mixRatio,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Mix Ratio") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mixDropdownExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = mixDropdownExpanded,
                                onDismissRequest = { mixDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("1:1.5:3") },
                                    onClick = {
                                        mixRatio = "1:1.5:3"
                                        mixDropdownExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("1:2:4") },
                                    onClick = {
                                        mixRatio = "1:2:4"
                                        mixDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Flight Dimensions", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = lengthFeet,
                            onValueChange = { lengthFeet = it },
                            label = { Text("Length (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lengthInches,
                            onValueChange = { lengthInches = it },
                            label = { Text("Length (in)") },
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
                            value = widthFeet,
                            onValueChange = { widthFeet = it },
                            label = { Text("Width (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = widthInches,
                            onValueChange = { widthInches = it },
                            label = { Text("Width (in)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = thicknessInches,
                            onValueChange = { thicknessInches = it },
                            label = { Text("Thickness (in)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Steps Configuration", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = risers,
                            onValueChange = { risers = it },
                            label = { Text("Risers (Count)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = treads,
                            onValueChange = { treads = it },
                            label = { Text("Treads (Count)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            value = riserHeightInches,
                            onValueChange = { riserHeightInches = it },
                            label = { Text("Riser H (in)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = treadWidthInches,
                            onValueChange = { treadWidthInches = it },
                            label = { Text("Tread W (in)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Reinforcement (Main & Distribution)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = mainBarMm,
                            onValueChange = { mainBarMm = it },
                            label = { Text("Main Bar (mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = mainSpacingInches,
                            onValueChange = { mainSpacingInches = it },
                            label = { Text("Spacing (in)") },
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
                            value = distBarMm,
                            onValueChange = { distBarMm = it },
                            label = { Text("Dist Bar (mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = distSpacingInches,
                            onValueChange = { distSpacingInches = it },
                            label = { Text("Spacing (in)") },
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
                                text = "Estimated Material Output (incl. 5% waste)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NavyPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Cement Bags:", fontSize = 13.sp, color = Color(0xFF475569))
                                Text("${calculationResult.cementBags.toInt()} bags", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Sand (CFT):", fontSize = 13.sp, color = Color(0xFF475569))
                                Text("${"%.1f".format(calculationResult.sandCft)} CFT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Aggregate (CFT):", fontSize = 13.sp, color = Color(0xFF475569))
                                Text("${"%.1f".format(calculationResult.aggregateCft)} CFT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            calculationResult.steelKgBySize.forEach { (dia, wt) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Steel (${dia}mm):", fontSize = 13.sp, color = Color(0xFF475569))
                                    Text("${"%.1f".format(wt)} kg", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldAccent)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Steel:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("${"%.1f".format(calculationResult.totalSteelKg)} kg", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
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
                                val steelParts = calculationResult.steelKgBySize.map { "${it.key}mm: ${"%.1f".format(it.value)} kg" }.joinToString(", ")
                                onSaveToProject(
                                    name.ifBlank { "Staircase" },
                                    calculationResult.details,
                                    calculationResult.cementBags,
                                    calculationResult.sandCft,
                                    calculationResult.aggregateCft,
                                    calculationResult.totalSteelKg,
                                    steelParts
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
