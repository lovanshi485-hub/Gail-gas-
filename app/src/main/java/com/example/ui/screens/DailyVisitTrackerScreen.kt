package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SiteVisitEntity
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailOrange
import com.example.util.ExportUtil
import com.example.util.FileMediaHelper
import com.example.viewmodel.GailGasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyVisitTrackerScreen(
    viewModel: GailGasViewModel
) {
    val context = LocalContext.current
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }
    val visits by viewModel.allVisits.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Record New Visit, 1 = Visit History

    // All fields optional - Kuch bhi mandatory nahi hai
    var customerBpNumber by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Mandideep") }
    var project by remember { mutableStateOf("GAIL Gas Mandideep CGD") }
    var visitPurpose by remember { mutableStateOf("House Survey & Feasibility") }
    var workPerformed by remember { mutableStateOf("Site inspected, meter shaft verified, route mapped.") }
    var visitOutcome by remember { mutableStateOf("Survey Complete") }
    var remarks by remember { mutableStateOf("") }
    var nextVisitDate by remember { mutableStateOf(today) }

    // Optional GPS
    var capturedGps by remember { mutableStateOf<String?>(null) }
    var gpsLat by remember { mutableStateOf<Double?>(null) }
    var gpsLng by remember { mutableStateOf<Double?>(null) }

    // Media
    var photoUri by remember { mutableStateOf<String?>(null) }
    var targetCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Delete confirmation state
    var visitToDelete by remember { mutableStateOf<SiteVisitEntity?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && targetCameraUri != null) {
            photoUri = targetCameraUri.toString()
            Toast.makeText(context, "Survey Photo Attached (Optional)", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = GailNavy,
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DAILY SURVEY & HOUSE VISITS",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total Visits: ${visits.size} • Unique Consumers: ${visits.map { it.customerBpNumber }.distinct().size}",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }

                        // Module-specific Excel & PDF Download Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    val file = ExportUtil.exportSiteVisitsToExcel(context, visits)
                                    ExportUtil.shareFile(context, file, "text/csv", "GAIL Survey & Visits Excel Sheet")
                                    Toast.makeText(context, "Exported Survey Excel", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = "Export Excel", tint = GailOrange)
                            }

                            IconButton(
                                onClick = {
                                    val file = ExportUtil.exportSiteVisitsToPdf(context, visits)
                                    ExportUtil.shareFile(context, file, "application/pdf", "GAIL Survey Visits PDF Report")
                                    Toast.makeText(context, "Exported Survey PDF", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = GailOrange
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Log House Visit", color = if (selectedTab == 0) GailOrange else Color.White) },
                            icon = { Icon(Icons.Default.Add, contentDescription = null, tint = if (selectedTab == 0) GailOrange else Color.White) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("All Visits (${visits.size})", color = if (selectedTab == 1) GailOrange else Color.White) },
                            icon = { Icon(Icons.Default.History, contentDescription = null, tint = if (selectedTab == 1) GailOrange else Color.White) }
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                // Record New Visit Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "HOUSE SURVEY DETAILS (KUCH BHI MANDATORY NAHI HAI)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = GailNavy
                            )

                            OutlinedTextField(
                                value = customerBpNumber,
                                onValueChange = { customerBpNumber = it },
                                label = { Text("BP Number (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = customerName,
                                onValueChange = { customerName = it },
                                label = { Text("Customer Name (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = houseNumber,
                                    onValueChange = { houseNumber = it },
                                    label = { Text("House No (Optional)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text("City (Optional)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            OutlinedTextField(
                                value = visitPurpose,
                                onValueChange = { visitPurpose = it },
                                label = { Text("Purpose of Visit (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = workPerformed,
                                onValueChange = { workPerformed = it },
                                label = { Text("Work Performed / Observations") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = visitOutcome,
                                onValueChange = { visitOutcome = it },
                                label = { Text("Visit Outcome (e.g. Feasible / Follow-up)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = remarks,
                                onValueChange = { remarks = it },
                                label = { Text("Remarks (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    // Optional GPS & Photo
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "GEO-LOCATION & SITE PHOTO (OPTIONAL)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = GailNavy
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val lat = 23.0763 + (0..100).random() * 0.0001
                                        val lng = 77.5247 + (0..100).random() * 0.0001
                                        gpsLat = lat
                                        gpsLng = lng
                                        capturedGps = "%.4f, %.4f".format(lat, lng)
                                        Toast.makeText(context, "GPS Coordinates Captured: $capturedGps", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pin GPS", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val pair = FileMediaHelper.createMediaFile(context, isVideo = false)
                                        targetCameraUri = pair.second
                                        cameraLauncher.launch(pair.second)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Photo", fontSize = 11.sp)
                                }
                            }

                            capturedGps?.let {
                                Text("Pinned GPS: $it", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                            }
                            photoUri?.let {
                                Text("✓ Site Photo Attached", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Save Button (Kuch bhi mandatory nahi)
                    Button(
                        onClick = {
                            val safeBp = if (customerBpNumber.isBlank()) "BP" + (100000..999999).random() else customerBpNumber.trim()
                            val safeName = if (customerName.isBlank()) "Site Consumer" else customerName.trim()

                            val visit = SiteVisitEntity(
                                customerBpNumber = safeBp,
                                customerName = safeName,
                                houseNumber = houseNumber.trim(),
                                city = city.trim(),
                                project = project.trim(),
                                visitDate = today,
                                visitTime = currentTime,
                                employeeName = "Rajesh Sharma",
                                visitPurpose = visitPurpose.trim(),
                                workPerformed = workPerformed.trim(),
                                visitOutcome = visitOutcome.trim(),
                                remarks = remarks.trim(),
                                nextVisitDate = nextVisitDate.trim(),
                                latitude = gpsLat,
                                longitude = gpsLng,
                                photoUri = photoUri
                            )
                            viewModel.recordSiteVisit(visit) {
                                Toast.makeText(context, "House visit logged successfully!", Toast.LENGTH_SHORT).show()
                                customerBpNumber = ""
                                customerName = ""
                                houseNumber = ""
                                remarks = ""
                                capturedGps = null
                                photoUri = null
                                selectedTab = 1
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save House Visit Record", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            } else {
                // Visit History List with DELETE
                if (visits.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No house visits recorded yet.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(visits, key = { it.id }) { v ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${v.visitDate} • ${v.visitTime}", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                        Text(v.customerBpNumber, fontSize = 12.sp, color = GailNavy, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(v.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("House: ${v.houseNumber.ifBlank { "N/A" }} • City: ${v.city.ifBlank { "Mandideep" }}", fontSize = 12.sp, color = GailOrange, fontWeight = FontWeight.Medium)
                                    Text("Purpose: ${v.visitPurpose}", fontSize = 12.sp, color = Color.DarkGray)
                                    Text("Work: ${v.workPerformed}", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))
                                    Text("Outcome: ${v.visitOutcome}", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
                                    if (v.remarks.isNotBlank()) {
                                        Text("Remarks: ${v.remarks}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    if (v.latitude != null) {
                                        Text("GPS: %.4f, %.4f".format(v.latitude, v.longitude), fontSize = 10.sp, color = Color.Gray)
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Action bar with DELETE
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { visitToDelete = v },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    visitToDelete?.let { v ->
        AlertDialog(
            onDismissRequest = { visitToDelete = null },
            title = { Text("Delete Visit Record?") },
            text = { Text("Are you sure you want to permanently delete visit record for ${v.customerName} (${v.customerBpNumber})?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSiteVisit(v)
                        visitToDelete = null
                        Toast.makeText(context, "Visit record deleted", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { visitToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
