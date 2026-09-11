package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.EstimateItemEntity
import com.example.data.entity.ProjectEntity
import com.example.model.ProjectSummaryData
import com.example.ui.dialogs.NewProjectDialog
import com.example.ui.dialogs.RatesSettingDialog
import com.example.ui.screens.calculators.BeamCalculatorDialog
import com.example.ui.screens.calculators.BrickworkCalculatorDialog
import com.example.ui.screens.calculators.ColumnCalculatorDialog
import com.example.ui.screens.calculators.CustomMaterialDialog
import com.example.ui.screens.calculators.FootingCalculatorDialog
import com.example.ui.screens.calculators.SlabCalculatorDialog
import com.example.ui.screens.calculators.StaircaseCalculatorDialog
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateBlue

data class CalculatorModule(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val badge: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    engineerName: String,
    userRole: String,
    projects: List<ProjectEntity>,
    activeProject: ProjectEntity?,
    activeItems: List<EstimateItemEntity>,
    summary: ProjectSummaryData?,
    onSelectProject: (ProjectEntity) -> Unit,
    onCreateProject: (title: String, client: String, location: String, notes: String, currency: String, cement: Double, sand: Double, agg: Double, steel: Double, brick: Double) -> Unit,
    onUpdateRates: (cement: Double, sand: Double, agg: Double, steel: Double, brick: Double, currency: String) -> Unit,
    onSaveEstimateItem: (category: String, name: String, specs: String, cement: Double, sand: Double, agg: Double, steelTotal: Double, steelBreakdown: String, bricks: Int, customQty: Double, customUnit: String, customRate: Double) -> Unit,
    onDeleteItem: (EstimateItemEntity) -> Unit,
    onNavigateToSummary: () -> Unit,
    onLogout: () -> Unit
) {
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showRatesDialog by remember { mutableStateOf(false) }
    var activeCalculatorId by remember { mutableStateOf<String?>(null) }
    var projectMenuExpanded by remember { mutableStateOf(false) }

    val modules = listOf(
        CalculatorModule("FOOTING", "Footing Calculator", "Concrete mix, dimensions & rebar mesh", Icons.Default.ViewInAr, "RCC"),
        CalculatorModule("COLUMN", "Column Calculator", "Rectangular/Circular, long & short, ties", Icons.Default.ViewColumn, "RCC"),
        CalculatorModule("BEAM", "Beam Calculator", "Main bars, extra top/bottom & stirrups", Icons.Default.ViewAgenda, "RCC"),
        CalculatorModule("SLAB", "Two-Way Slab", "Thickness, mix, main rebar & extra top sets", Icons.Default.GridView, "RCC"),
        CalculatorModule("STAIRCASE", "Staircase Calculator", "Waist slab, risers, treads & 2-layer steel", Icons.Default.Architecture, "RCC"),
        CalculatorModule("BRICKWORK", "Brickwork & Plaster", "Bricks count, mortar & 2-sided plaster", Icons.Default.Build, "Civil"),
        CalculatorModule("CUSTOM", "Add Material & Rate", "Custom quantity, unit & rate for project", Icons.Default.Paid, "BOQ")
    )

    val cur = activeProject?.currencySymbol ?: "৳"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BUILDING ESTIMATE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "$engineerName ($userRole)",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                },
                actions = {
                    // Protected session indicator
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Protected",
                        tint = GoldLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    // Project Switcher
                    Box {
                        IconButton(onClick = { projectMenuExpanded = true }) {
                            Icon(Icons.Default.Folder, contentDescription = "Switch Project", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = projectMenuExpanded,
                            onDismissRequest = { projectMenuExpanded = false }
                        ) {
                            projects.forEach { proj ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            proj.title,
                                            fontWeight = if (proj.id == activeProject?.id) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onSelectProject(proj)
                                        projectMenuExpanded = false
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("+ Create New Project", color = NavyPrimary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    projectMenuExpanded = false
                                    showNewProjectDialog = true
                                }
                            )
                        }
                    }

                    // Rates dialog action
                    IconButton(onClick = { showRatesDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Rates", tint = Color.White)
                    }

                    // Logout
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFE2E8F0))
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
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Active Project Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ACTIVE PROJECT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = activeProject?.title ?: "Select a project",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (!activeProject?.clientName.isNullOrBlank()) {
                                    Text(
                                        text = "Client: ${activeProject?.clientName}",
                                        fontSize = 12.sp,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }

                            // Items count chip
                            Surface(
                                color = Color(0x3338BDF8),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${activeItems.size} items",
                                    fontSize = 11.sp,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Grand Total Cost Display
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E3A5F), shape = RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ESTIMATED TOTAL COST", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                Text(
                                    text = "$cur ${"%,.2f".format(summary?.grandTotalCost ?: 0.0)}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldLight
                                )
                            }

                            Button(
                                onClick = onNavigateToSummary,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldAccent,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("BOQ / PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Secondary actions row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showRatesDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Paid, contentDescription = null, tint = GoldLight, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rates", color = Color.White, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { showNewProjectDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Proj", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Calculators Section Header
            item {
                Text(
                    text = "Engineering Estimation Calculators",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }

            // Calculators List Cards
            items(modules) { module ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeCalculatorId = module.id },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = CircleShape,
                            color = if (module.badge == "RCC") Color(0xFFEFF6FF) else Color(0xFFFEF3C7)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = module.icon,
                                    contentDescription = module.title,
                                    tint = if (module.badge == "RCC") NavyPrimary else GoldAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(module.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = module.badge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334155),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(module.description, fontSize = 11.sp, color = Color(0xFF64748B))
                        }

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Open",
                            tint = NavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Recent Estimated Items Header
            if (activeItems.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimated Items in Project (${activeItems.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )

                        Text(
                            text = "View BOQ →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateBlue,
                            modifier = Modifier.clickable { onNavigateToSummary() }
                        )
                    }
                }

                items(activeItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = item.category,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyPrimary,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                if (item.specsDescription.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.specsDescription,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$cur ${"%,.2f".format(item.subtotalCost)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyPrimary
                                )
                            }

                            IconButton(onClick = { onDeleteItem(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFCBD5E1))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Calculator Dialogs
    when (activeCalculatorId) {
        "FOOTING" -> {
            FootingCalculatorDialog(
                onDismiss = { activeCalculatorId = null },
                onSaveToProject = { name, specs, cement, sand, agg, steel, breakdown ->
                    onSaveEstimateItem("FOOTING", name, specs, cement, sand, agg, steel, breakdown, 0, 0.0, "", 0.0)
                }
            )
        }
        "COLUMN" -> {
            ColumnCalculatorDialog(
                onDismiss = { activeCalculatorId = null },
                onSaveToProject = { name, specs, cement, sand, agg, steel, breakdown ->
                    onSaveEstimateItem("COLUMN", name, specs, cement, sand, agg, steel, breakdown, 0, 0.0, "", 0.0)
                }
            )
        }
        "BEAM" -> {
            BeamCalculatorDialog(
                onDismiss = { activeCalculatorId = null },
                onSaveToProject = { name, specs, cement, sand, agg, steel, breakdown ->
                    onSaveEstimateItem("BEAM", name, specs, cement, sand, agg, steel, breakdown, 0, 0.0, "", 0.0)
                }
            )
        }
        "SLAB" -> {
            SlabCalculatorDialog(
                onDismiss = { activeCalculatorId = null },
                onSaveToProject = { name, specs, cement, sand, agg, steel, breakdown ->
                    onSaveEstimateItem("SLAB", name, specs, cement, sand, agg, steel, breakdown, 0, 0.0, "", 0.0)
                }
            )
        }
        "STAIRCASE" -> {
            StaircaseCalculatorDialog(
                onDismiss = { activeCalculatorId = null },
                onSaveToProject = { name, specs, cement, sand, agg, steel, breakdown ->
                    onSaveEstimateItem("STAIRCASE", name, specs, cement, sand, agg, steel, breakdown, 0, 0.0, "", 0.0)
                }
            )
        }
        "BRICKWORK" -> {
            BrickworkCalculatorDialog(
                onDismiss = { activeCalculatorId = null },
                onSaveToProject = { name, specs, cement, sand, bricks ->
                    onSaveEstimateItem("BRICKWORK", name, specs, cement, sand, 0.0, 0.0, "", bricks, 0.0, "", 0.0)
                }
            )
        }
        "CUSTOM" -> {
            CustomMaterialDialog(
                currencySymbol = cur,
                onDismiss = { activeCalculatorId = null },
                onSaveItem = { name, specs, qty, u, rate ->
                    onSaveEstimateItem("CUSTOM", name, specs, 0.0, 0.0, 0.0, 0.0, "", 0, qty, u, rate)
                }
            )
        }
    }

    // New Project Dialog
    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onCreateProject = onCreateProject
        )
    }

    // Rates Dialog
    if (showRatesDialog && activeProject != null) {
        RatesSettingDialog(
            project = activeProject,
            onDismiss = { showRatesDialog = false },
            onSaveRates = onUpdateRates
        )
    }
}
