package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.EstimateItemEntity
import com.example.model.ProjectSummaryData
import com.example.pdf.PdfReportGenerator
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSummaryScreen(
    summary: ProjectSummaryData?,
    engineerName: String,
    onBack: () -> Unit,
    onDeleteItem: (EstimateItemEntity) -> Unit
) {
    val context = LocalContext.current

    if (summary == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No project selected")
        }
        return
    }

    val cur = summary.project.currencySymbol

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Bill of Quantities (BOQ)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = summary.project.title,
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val uri = PdfReportGenerator.generateAndShare(context, summary, engineerName)
                        if (uri != null) {
                            Toast.makeText(context, "PDF Report generated successfully!", Toast.LENGTH_SHORT).show()
                            PdfReportGenerator.openOrSharePdf(context, uri)
                        } else {
                            Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Download PDF",
                            tint = GoldLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // Security Banner
                Surface(
                    color = Color(0x15F59E0B),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Confidential Estimate • Protected Session",
                            fontSize = 11.sp,
                            color = GoldAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Grand Total Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "ESTIMATED GRAND TOTAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$cur ${"%,.2f".format(summary.grandTotalCost)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Client", fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(
                                    text = summary.project.clientName.ifBlank { "N/A" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Site Location", fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(
                                    text = summary.project.location.ifBlank { "Not Specified" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // PDF Action Button
                        Button(
                            onClick = {
                                val uri = PdfReportGenerator.generateAndShare(context, summary, engineerName)
                                if (uri != null) {
                                    Toast.makeText(context, "PDF Report Generated!", Toast.LENGTH_SHORT).show()
                                    PdfReportGenerator.openOrSharePdf(context, uri)
                                } else {
                                    Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldAccent,
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download / Share Estimate PDF", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Material BOQ Breakdown Table
            item {
                Text(
                    text = "Materials Bill of Quantities (BOQ)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text("Material", modifier = Modifier.weight(1.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Qty", modifier = Modifier.weight(1.2f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Rate", modifier = Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Amount", modifier = Modifier.weight(1.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (summary.materialRows.isEmpty()) {
                            Text(
                                text = "No items added yet. Calculate structural elements or add custom materials below!",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            summary.materialRows.forEachIndexed { idx, row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(row.materialName, modifier = Modifier.weight(1.8f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("${"%,.1f".format(row.quantity)} ${row.unit}", modifier = Modifier.weight(1.2f), fontSize = 11.sp, color = Color(0xFF475569))
                                    Text("${"%,.1f".format(row.unitRate)}", modifier = Modifier.weight(1f), fontSize = 11.sp, color = Color(0xFF475569))
                                    Text("$cur ${"%,.1f".format(row.totalCost)}", modifier = Modifier.weight(1.4f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                if (idx < summary.materialRows.size - 1) {
                                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }

            // Steel by Diameter Table
            if (summary.steelBySize.isNotEmpty()) {
                item {
                    Text(
                        text = "Steel Reinforcement Breakdown",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            summary.steelBySize.forEach { (dia, wt) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${dia}mm MS Deformed Rebar", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text(
                                        "${"%,.1f".format(wt)} kg (${"%,.2f".format(wt / 1000.0)} Tons)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent
                                    )
                                }
                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

            // Estimated Items List
            item {
                Text(
                    text = "Project Structural Components (${summary.itemsByCategory.values.sumOf { it.size }} items)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }

            summary.itemsByCategory.forEach { (category, items) ->
                item {
                    Text(
                        text = "• $category",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(items) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (item.specsDescription.isNotBlank()) {
                                    Text(
                                        text = item.specsDescription,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 2
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Subtotal: $cur ${"%,.2f".format(item.subtotalCost)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NavyPrimary
                                )
                            }

                            IconButton(onClick = { onDeleteItem(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove item", tint = Color.LightGray)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
