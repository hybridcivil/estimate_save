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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.model.BeamBarInput
import com.example.model.BeamCalculator
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary

@Composable
fun BeamCalculatorDialog(
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
    var name by remember { mutableStateOf("B-1") }
    var numberOfBeams by remember { mutableStateOf("2") }
    var widthInches by remember { mutableStateOf("12") }
    var depthInches by remember { mutableStateOf("18") }
    var lengthFt by remember { mutableStateOf("12.0") }
    var stirrupSpacingInches by remember { mutableStateOf("6") }

    val mainBars = remember {
        mutableStateListOf(
            BeamBarInput(sizeMm = 16, qty = 2)
        )
    }

    var extraTopNos by remember { mutableStateOf("2") }
    var extraTopDia by remember { mutableStateOf("16") }

    var extraBottomNos by remember { mutableStateOf("2") }
    var extraBottomDia by remember { mutableStateOf("16") }

    val lVal = lengthFt.toDoubleOrNull() ?: 12.0
    val topAutoLength = "%.2f".format(lVal / 3.0)
    val bottomAutoLength = "%.2f".format(lVal / 2.0)

    val calculationResult by remember(
        name, numberOfBeams, widthInches, depthInches, lengthFt, stirrupSpacingInches,
        mainBars.toList(), extraTopNos, extraTopDia, extraBottomNos, extraBottomDia
    ) {
        val w = widthInches.toDoubleOrNull() ?: 0.0
        val d = depthInches.toDoubleOrNull() ?: 0.0
        val l = lengthFt.toDoubleOrNull() ?: 0.0
        val nos = numberOfBeams.toIntOrNull() ?: 0
        val sp = stirrupSpacingInches.toDoubleOrNull() ?: 6.0
        val etNos = extraTopNos.toIntOrNull() ?: 0
        val etDia = extraTopDia.toIntOrNull() ?: 0
        val ebNos = extraBottomNos.toIntOrNull() ?: 0
        val ebDia = extraBottomDia.toIntOrNull() ?: 0

        mutableStateOf(
            BeamCalculator.calculate(
                widthInches = w,
                depthInches = d,
                lengthFt = l,
                numberOfBeams = nos,
                stirrupSpacingInches = sp,
                mainBars = mainBars,
                extraTopNos = etNos,
                extraTopDia = etDia,
                extraBottomNos = ebNos,
                extraBottomDia = ebDia
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
                // Header
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
                                text = "Beam Material Calculator",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Concrete, Main Bars, Extra Top/Bottom & Stirrups",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Scrollable Body
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
                            label = { Text("Beam ID") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = numberOfBeams,
                            onValueChange = { numberOfBeams = it },
                            label = { Text("Number of Beams") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Beam Dimensions", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = widthInches,
                            onValueChange = { widthInches = it },
                            label = { Text("Width (in)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = depthInches,
                            onValueChange = { depthInches = it },
                            label = { Text("Depth (in)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lengthFt,
                            onValueChange = { lengthFt = it },
                            label = { Text("Length (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = stirrupSpacingInches,
                        onValueChange = { stirrupSpacingInches = it },
                        label = { Text("10mm Stirrup Spacing (in c/c)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Main Reinforcement
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Main Longitudinal Rebars", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        FilledTonalButton(
                            onClick = { mainBars.add(BeamBarInput(sizeMm = 16, qty = 2)) },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Bar", fontSize = 11.sp)
                        }
                    }

                    mainBars.forEachIndexed { index, bar ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = bar.sizeMm.toString(),
                                onValueChange = {
                                    val s = it.toIntOrNull() ?: 0
                                    mainBars[index] = bar.copy(sizeMm = s)
                                },
                                label = { Text("Bar Size (mm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = bar.qty.toString(),
                                onValueChange = {
                                    val q = it.toIntOrNull() ?: 0
                                    mainBars[index] = bar.copy(qty = q)
                                },
                                label = { Text("Quantity") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            if (mainBars.size > 1) {
                                IconButton(
                                    onClick = { mainBars.removeAt(index) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Extra Top Bars
                    Text("Extra Top Bars (per support, auto length = L/3 = ${topAutoLength}ft)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = extraTopNos,
                            onValueChange = { extraTopNos = it },
                            label = { Text("Top Bars Nos") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = extraTopDia,
                            onValueChange = { extraTopDia = it },
                            label = { Text("Top Bar Dia (mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Extra Bottom Bars
                    Text("Extra Bottom Bars (mid-span, auto length = L/2 = ${bottomAutoLength}ft)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = extraBottomNos,
                            onValueChange = { extraBottomNos = it },
                            label = { Text("Bottom Bars Nos") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = extraBottomDia,
                            onValueChange = { extraBottomDia = it },
                            label = { Text("Bottom Bar Dia (mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                    Text(if (dia == 10) "10mm Stirrups:" else "Rebar (${dia}mm):", fontSize = 13.sp, color = Color(0xFF475569))
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

                // Footer
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
                                    name.ifBlank { "Beam" },
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
