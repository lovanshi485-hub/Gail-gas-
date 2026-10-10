package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomerEntity
import com.example.model.FeasibilityStatus
import com.example.model.GasInStatus
import com.example.model.RegistrationStatus
import com.example.model.TnfStatus
import com.example.ui.components.SyncStatusBadge
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailOrange
import com.example.ui.theme.GailOrangeLight
import com.example.util.ExportUtil
import com.example.viewmodel.GailGasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: GailGasViewModel,
    onNavigateToCustomers: () -> Unit,
    onNavigateToFeasibility: () -> Unit,
    onNavigateToTnf: () -> Unit,
    onNavigateToGasIn: () -> Unit,
    onNavigateToVisitTracker: () -> Unit,
    onNavigateToDpr: () -> Unit,
    onCustomerClick: (CustomerEntity) -> Unit
) {
    val context = LocalContext.current
    val customers by viewModel.allCustomers.collectAsState()
    val visits by viewModel.allVisits.collectAsState()
    val feasibilities by viewModel.allFeasibilities.collectAsState()
    val tnfCases by viewModel.allTnfCases.collectAsState()
    val gasInRecords by viewModel.allGasInRecords.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var selectedDateFilter by remember { mutableStateOf("Today") }

    // Calculated Dashboard Metrics
    val todayVisits = visits.filter { if (selectedDateFilter == "Today") it.visitDate == todayStr else true }
    val housesVisitedCount = todayVisits.size
    val uniqueCustomersCount = todayVisits.map { it.customerBpNumber }.distinct().size

    val feasibleCases = feasibilities.size
    val nonFeasibleCases = 0
    val pendingFeasibility = customers.count { it.feasibilityStatus == "PENDING" }
    val totalFeasibilityCompleted = feasibilities.size

    val registrationCompleted = customers.count { it.registrationStatus == RegistrationStatus.COMPLETED.name }
    val pendingRegistration = customers.count { it.registrationStatus == RegistrationStatus.PENDING.name }

    val openTnfCases = tnfCases.count { it.status == TnfStatus.OPEN.name }
    val closedTnfCases = tnfCases.count { it.status == TnfStatus.CLOSED.name }

    val gasInCompleted = gasInRecords.count { it.gasInStatus == GasInStatus.COMPLETED.name }
    val gasInPending = customers.count { it.gasInDate.isBlank() && it.feasibilityStatus == FeasibilityStatus.FEASIBLE.name }

    val totalComplaints = customers.count { it.complaintNumber.isNotBlank() }
    val pendingFollowups = customers.count { it.nextFollowUpDate.isNotBlank() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Header & Supervisor Profile
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GailNavy)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GAIL GAS PMC PRO",
                                color = GailOrangeLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = currentUser.name,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentUser.designation} • ${currentUser.employeeId}",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp
                            )
                        }

                        SyncStatusBadge(
                            syncState = syncState,
                            onSyncClick = { viewModel.triggerSync() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Filters & Project
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Today", "This Week", "All Time").forEach { filter ->
                            FilterChip(
                                selected = selectedDateFilter == filter,
                                onClick = { selectedDateFilter = filter },
                                label = { Text(filter, fontSize = 12.sp) }
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            onClick = {
                                viewModel.triggerSync()
                                Toast.makeText(context, "Refreshed live PMC field records", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "QUICK ACTIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        QuickActionButton(
                            title = "House Visit",
                            subtitle = "Record Visit",
                            icon = Icons.Default.LocationOn,
                            color = GailOrange,
                            onClick = onNavigateToVisitTracker
                        )
                    }
                    item {
                        QuickActionButton(
                            title = "Feasibility",
                            subtitle = "New Survey",
                            icon = Icons.Default.Assignment,
                            color = Color(0xFF198754),
                            onClick = onNavigateToFeasibility
                        )
                    }
                    item {
                        QuickActionButton(
                            title = "Gas-in / Meter",
                            subtitle = "Commission",
                            icon = Icons.Default.Speed,
                            color = Color(0xFF0D6EFD),
                            onClick = onNavigateToGasIn
                        )
                    }
                    item {
                        QuickActionButton(
                            title = "DPR Report",
                            subtitle = "Daily Progress",
                            icon = Icons.Default.Description,
                            color = Color(0xFF6F42C1),
                            onClick = onNavigateToDpr
                        )
                    }
                }
            }
        }

        // Section: Today's Field Visit Metrics
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "FIELD VISIT HIGHLIGHTS ($selectedDateFilter)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(
                        title = "Houses Visited",
                        value = "$housesVisitedCount",
                        subtitle = "Cumulative site visits",
                        icon = Icons.Default.LocationOn,
                        color = GailOrange,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Unique Customers",
                        value = "$uniqueCustomersCount",
                        subtitle = "Distinct BP addresses",
                        icon = Icons.Default.Person,
                        color = GailNavy,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: Feasibility & Registration Grid
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "FEASIBILITY & REGISTRATION SUMMARY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(
                        title = "Feasibility Done",
                        value = "$totalFeasibilityCompleted",
                        subtitle = "Total surveyed",
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Feasible Cases",
                        value = "$feasibleCases",
                        subtitle = "Approved pipeline route",
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF388E3C),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(
                        title = "Non-Feasible",
                        value = "$nonFeasibleCases",
                        subtitle = "Technical limitation",
                        icon = Icons.Default.Error,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Pending Feas.",
                        value = "$pendingFeasibility",
                        subtitle = "Awaiting visit",
                        icon = Icons.Default.Assignment,
                        color = Color(0xFFF57C00),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(
                        title = "Registration Done",
                        value = "$registrationCompleted",
                        subtitle = "Agreements signed",
                        icon = Icons.Default.Assignment,
                        color = Color(0xFF1565C0),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Pending Reg.",
                        value = "$pendingRegistration",
                        subtitle = "Documents pending",
                        icon = Icons.Default.DateRange,
                        color = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: TNF, Gas-In & Operational Backlog
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "TNF, GAS-IN & BACKLOG CONTROL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(
                        title = "Open TNF Cases",
                        value = "$openTnfCases",
                        subtitle = "Pending resolution",
                        icon = Icons.Default.Warning,
                        color = Color(0xFFC2185B),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Closed TNF Cases",
                        value = "$closedTnfCases",
                        subtitle = "Cleared & bypassed",
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF00796B),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(
                        title = "Gas-in Completed",
                        value = "$gasInCompleted",
                        subtitle = "Meters live & commissioned",
                        icon = Icons.Default.LocalGasStation,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Gas-in Pending",
                        value = "$gasInPending",
                        subtitle = "Riser line ready",
                        icon = Icons.Default.Speed,
                        color = Color(0xFF0288D1),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(
                        title = "Total Complaints",
                        value = "$totalComplaints",
                        subtitle = "Logged in system",
                        icon = Icons.Default.Build,
                        color = Color(0xFF5D4037),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Pending Follow-ups",
                        value = "$pendingFollowups",
                        subtitle = "Scheduled visits",
                        icon = Icons.Default.DateRange,
                        color = Color(0xFF512DA8),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: Project-wise progress bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PROJECT PROGRESS - BHOPAL CGD PHASE 2",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GailNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Target Connections: 1,500 | Current Commissioned: $gasInCompleted",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val progressRatio = (gasInCompleted.toFloat() / 20f).coerceIn(0.1f, 1.0f)
                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GailOrange,
                        trackColor = Color(0xFFFFE0B2),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Assigned Supervisor: ${currentUser.name} (${currentUser.assignedProject})",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // Export and Excel quick triggers
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val file = ExportUtil.exportCustomersToExcel(context, customers)
                        ExportUtil.shareFile(context, file, "text/csv", "Export GAIL PMC Excel")
                        Toast.makeText(context, "Exported XLSX/CSV file", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GailNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export Excel", fontSize = 12.sp)
                }

                Button(
                    onClick = onNavigateToCustomers,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Customer List", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(135.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = color.copy(alpha = 0.12f),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(2.dp))
                Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 9.sp, color = Color(0xFF94A3B8), maxLines = 1)
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
        }
    }
}
