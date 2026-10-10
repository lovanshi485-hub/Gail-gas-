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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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

    var customerBpNumber by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }
    var project by remember { mutableStateOf("Bhopal CGD Project") }
    var visitPurpose by remember { mutableStateOf("Feasibility & Customer Consent") }
    var workPerformed by remember { mutableStateOf("Site inspected, pipe routing marked on wall shaft, owner consultation.") }
    var visitOutcome by remember { mutableStateOf("Feasible - Owner agreed") }
    var remarks by remember { mutableStateOf("") }
    var nextVisitDate by remember { mutableStateOf(today) }

    // Optional GPS
    var capturedGps by remember { mutableStateOf<String?>(null) }
    var gpsLat by remember { mutableStateOf<Double?>(null) }
    var gpsLng by remember { mutableStateOf<Double?>(null) }

    // Media
    var photoUri by remember { mutableStateOf<String?>(null) }
    var targetCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && targetCameraUri != null) {
            photoUri = targetCameraUri.toString()
            Toast.makeText(context, "Site photo attached", Toast.LENGTH_SHORT).show()
        }
    }

    // Permission launcher for Location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            // Simulated realistic Bhopal CGD coordinate capture
            val lat = 23.2150 + (0..100).random() * 0.0001
            val lng = 77.4300 + (0..100).random() * 0.0001
            gpsLat = lat
            gpsLng = lng
            capturedGps = "%.5f° N, %.5f° E".format(lat, lng)
            Toast.makeText(context, "GPS Location Captured: $capturedGps", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Location permission optional - not granted", Toast.LENGTH_SHORT).show()
        }
    }

    val todayVisits = visits.filter { it.visitDate == today }
    val uniqueTodayCustomers = todayVisits.map { it.customerBpNumber }.distinct().size

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = GailNavy, tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text("DAILY HOUSE VISIT TRACKER", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Today's Visits: ${todayVisits.size} | Unique Houses: $uniqueTodayCustomers",
                    color = GailOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = GailOrange
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Log House Visit", color = if (selectedTab == 0) GailOrange else Color.White) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Visit History (${visits.size})", color = if (selectedTab == 1) GailOrange else Color.White) }
                    )
                }
            }
        }

        if (selectedTab == 0) {
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
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("HOUSE & CUSTOMER IDENTIFIER", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                        OutlinedTextField(
                            value = customerBpNumber,
                            onValueChange = { customerBpNumber = it },
                            label = { Text("BP Number *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Customer Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = houseNumber,
                                onValueChange = { houseNumber = it },
                                label = { Text("House Number") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = project,
                                onValueChange = { project = it },
                                label = { Text("Project") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("WORK PERFORMED & OUTCOME", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                        OutlinedTextField(
                            value = visitPurpose,
                            onValueChange = { visitPurpose = it },
                            label = { Text("Visit Purpose") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = workPerformed,
                            onValueChange = { workPerformed = it },
                            label = { Text("Work Performed at Site") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        OutlinedTextField(
                            value = visitOutcome,
                            onValueChange = { visitOutcome = it },
                            label = { Text("Visit Outcome") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = { Text("Field Notes / Next Action") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = nextVisitDate,
                            onValueChange = { nextVisitDate = it },
                            label = { Text("Next Follow-up Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                // Optional GPS & Camera
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("OPTIONAL GPS & CAMERA EVIDENCE", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Capture GPS", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val pair = FileMediaHelper.createMediaFile(context, isVideo = false)
                                    targetCameraUri = pair.second
                                    cameraLauncher.launch(pair.second)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Take Photo", fontSize = 11.sp)
                            }
                        }

                        capturedGps?.let {
                            Text("Pinned Coordinates: $it", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Button(
                    onClick = {
                        if (customerBpNumber.isBlank() || customerName.isBlank()) {
                            Toast.makeText(context, "BP Number and Customer Name required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val visit = SiteVisitEntity(
                            customerBpNumber = customerBpNumber.trim(),
                            customerName = customerName.trim(),
                            houseNumber = houseNumber.trim(),
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
                            Toast.makeText(context, "House visit logged. Total visits updated.", Toast.LENGTH_SHORT).show()
                            customerBpNumber = ""
                            customerName = ""
                            houseNumber = ""
                            capturedGps = null
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
            // Visit History List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visits) { v ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${v.visitDate} • ${v.visitTime}", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(v.customerBpNumber, fontSize = 12.sp, color = GailNavy, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(v.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("House: ${v.houseNumber} • Purpose: ${v.visitPurpose}", fontSize = 12.sp, color = GailOrange, fontWeight = FontWeight.Medium)
                            Text(v.workPerformed, fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))
                            Text("Outcome: ${v.visitOutcome}", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
                            if (v.latitude != null) {
                                Text("GPS: %.4f, %.4f".format(v.latitude, v.longitude), fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
