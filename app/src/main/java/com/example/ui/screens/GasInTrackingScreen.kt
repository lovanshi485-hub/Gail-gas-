package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.GasInEntity
import com.example.model.GasInStatus
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailOrange
import com.example.util.ExportUtil
import com.example.util.FileMediaHelper
import com.example.viewmodel.GailGasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GasInTrackingScreen(
    viewModel: GailGasViewModel
) {
    val context = LocalContext.current
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val gasInRecords by viewModel.allGasInRecords.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = New Commissioning, 1 = Commissioned Records

    // All fields optional - Kuch bhi mandatory nahi hai
    var bpNumber by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var meterNumber by remember { mutableStateOf("21241" + (100..999).random()) }
    var meterMake by remember { mutableStateOf("Raychem RPG G1.6") }
    var meterReading by remember { mutableStateOf("0.022") }
    var meterInstallationDate by remember { mutableStateOf(today) }
    var regulatorDetails by remember { mutableStateOf("21 mbar Regulator") }
    var leakTestStatus by remember { mutableStateOf("Passed - 0 Drop in 15 Min") }
    var remarks by remember { mutableStateOf("Burner flame test blue. Line commissioned.") }

    // JMR Photo & Meter Photo (Both completely optional / non-mandatory)
    var jmrPhotoUriString by remember { mutableStateOf<String?>(null) }
    var meterPhotoUriString by remember { mutableStateOf<String?>(null) }
    var cameraTargetUri by remember { mutableStateOf<Uri?>(null) }
    var capturingForJmr by remember { mutableStateOf(true) }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<GasInEntity?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraTargetUri != null) {
            if (capturingForJmr) {
                jmrPhotoUriString = cameraTargetUri.toString()
                Toast.makeText(context, "JMR Card Photo Attached (Optional)", Toast.LENGTH_SHORT).show()
            } else {
                meterPhotoUriString = cameraTargetUri.toString()
                Toast.makeText(context, "Gas Meter Photo Attached (Optional)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val copied = FileMediaHelper.copyUriToAppStorage(context, it)
            if (capturingForJmr) {
                jmrPhotoUriString = copied ?: it.toString()
                Toast.makeText(context, "JMR Photo Selected from Gallery", Toast.LENGTH_SHORT).show()
            } else {
                meterPhotoUriString = copied ?: it.toString()
                Toast.makeText(context, "Meter Photo Selected from Gallery", Toast.LENGTH_SHORT).show()
            }
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
                                text = "GAS-IN & JMR COMMISSIONING",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Meters Live: ${gasInRecords.size} • Safety Leak Checks Certified",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }

                        // Module-specific Excel & PDF Download Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    val file = ExportUtil.exportGasInToExcel(context, gasInRecords)
                                    ExportUtil.shareFile(context, file, "text/csv", "GAIL Gas-In Commissioning Excel")
                                    Toast.makeText(context, "Exported Gas-In Excel", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = "Export Excel", tint = GailOrange)
                            }

                            IconButton(
                                onClick = {
                                    val file = ExportUtil.exportGasInToPdf(context, gasInRecords)
                                    ExportUtil.shareFile(context, file, "application/pdf", "GAIL Gas-In PDF Report")
                                    Toast.makeText(context, "Exported Gas-In PDF", Toast.LENGTH_SHORT).show()
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
                            text = { Text("Commission Gas-in", color = if (selectedTab == 0) GailOrange else Color.White) },
                            icon = { Icon(Icons.Default.Add, contentDescription = null, tint = if (selectedTab == 0) GailOrange else Color.White) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Records (${gasInRecords.size})", color = if (selectedTab == 1) GailOrange else Color.White) },
                            icon = { Icon(Icons.Default.History, contentDescription = null, tint = if (selectedTab == 1) GailOrange else Color.White) }
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                // Commissioning Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Consumer identification card
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
                                text = "CONSUMER DETAILS (KUCH BHI MANDATORY NAHI HAI)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = GailNavy
                            )

                            OutlinedTextField(
                                value = bpNumber,
                                onValueChange = { bpNumber = it },
                                label = { Text("BP Number (Optional)") },
                                placeholder = { Text("e.g. 1002348") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = customerName,
                                onValueChange = { customerName = it },
                                label = { Text("Customer Name (Optional)") },
                                placeholder = { Text("Consumer Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    // Meter & Regulator details
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
                                text = "METER & JMR SPECIFICATIONS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = GailNavy
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = meterNumber,
                                    onValueChange = { meterNumber = it },
                                    label = { Text("Meter No.") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = meterReading,
                                    onValueChange = { meterReading = it },
                                    label = { Text("Reading (Initial)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            OutlinedTextField(
                                value = meterMake,
                                onValueChange = { meterMake = it },
                                label = { Text("Meter Make & Model") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = regulatorDetails,
                                onValueChange = { regulatorDetails = it },
                                label = { Text("Regulator Details") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = leakTestStatus,
                                onValueChange = { leakTestStatus = it },
                                label = { Text("Safety Leak Test Result") },
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

                    // Optional Media: JMR Photo & Meter Photo
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "JMR CARD & METER PHOTOS (OPTIONAL - MANDATORY NAHI HAI)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = GailNavy
                            )

                            // 1. JMR Card Photo Controls
                            Text("1. Joint Meter Reading (JMR) Card Photo (Optional):", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        capturingForJmr = true
                                        val pair = FileMediaHelper.createMediaFile(context, isVideo = false)
                                        cameraTargetUri = pair.second
                                        cameraLauncher.launch(pair.second)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("JMR Camera", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        capturingForJmr = true
                                        galleryLauncher.launch("image/*")
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("JMR Gallery", fontSize = 11.sp)
                                }
                            }

                            jmrPhotoUriString?.let { uriStr ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, GailOrange, RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = uriStr,
                                        contentDescription = "JMR Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { jmrPhotoUriString = null },
                                        modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(26.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
                                    }
                                }
                            }

                            // 2. Gas Meter Photo Controls
                            Text("2. Installed Gas Meter Dial Photo (Optional):", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        capturingForJmr = false
                                        val pair = FileMediaHelper.createMediaFile(context, isVideo = false)
                                        cameraTargetUri = pair.second
                                        cameraLauncher.launch(pair.second)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Meter Camera", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        capturingForJmr = false
                                        galleryLauncher.launch("image/*")
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Meter Gallery", fontSize = 11.sp)
                                }
                            }

                            meterPhotoUriString?.let { uriStr ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFF198754), RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = uriStr,
                                        contentDescription = "Meter Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { meterPhotoUriString = null },
                                        modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(26.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }

                    // Confirm Commissioning Button (Nothing is mandatory)
                    Button(
                        onClick = {
                            showConfirmationDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.LocalGasStation, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm Gas-in Commissioning", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            } else {
                // Commissioned Gas-In / JMR Records with DELETE
                if (gasInRecords.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No gas-in commissioning records yet.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(gasInRecords, key = { it.id }) { g ->
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
                                        Text(
                                            text = if (g.bpNumber.isNotBlank()) "BP: ${g.bpNumber}" else "Gas-in Entry #${g.id}",
                                            fontWeight = FontWeight.Bold,
                                            color = GailNavy,
                                            fontSize = 14.sp
                                        )
                                        Surface(
                                            color = Color(0xFFE8F5E9),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "GAS-IN LIVE",
                                                color = Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(g.customerName.ifBlank { "Consumer Record" }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Meter: ${g.meterNumber} (${g.meterMake}) • Reading: ${g.meterReading}", fontSize = 12.sp, color = GailOrange, fontWeight = FontWeight.Medium)
                                    Text("Leak Check: ${g.leakTestStatus}", fontSize = 11.sp, color = Color(0xFF2E7D32))
                                    if (g.remarks.isNotBlank()) {
                                        Text("Remarks: ${g.remarks}", fontSize = 11.sp, color = Color.Gray)
                                    }

                                    // Media indicator badges
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (!g.jmrPhotoUri.isNullOrBlank()) {
                                            Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(4.dp)) {
                                                Text("✓ JMR Card Photo", fontSize = 10.sp, color = Color(0xFFE65100), modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        if (!g.meterPhotoUri.isNullOrBlank()) {
                                            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                                                Text("✓ Meter Photo", fontSize = 10.sp, color = Color(0xFF2E7D32), modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.weight(1f))

                                        // DELETE button
                                        IconButton(
                                            onClick = { recordToDelete = g },
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

    // Confirmation Dialog for Commissioning
    if (showConfirmationDialog) {
        val safeBp = if (bpNumber.isBlank()) "BP" + (100000..999999).random() else bpNumber.trim()
        val safeName = if (customerName.isBlank()) "Consumer-${safeBp.takeLast(4)}" else customerName.trim()

        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirm Gas-in Commissioning?") },
            text = { Text("BP $safeBp ($safeName) ke liye Gas-in confirm karein. Photos optional hain.") },
            confirmButton = {
                Button(
                    onClick = {
                        val record = GasInEntity(
                            bpNumber = safeBp,
                            customerName = safeName,
                            requestDate = today,
                            completionDate = today,
                            gasInStatus = GasInStatus.COMPLETED.name,
                            meterNumber = meterNumber.trim(),
                            meterMake = meterMake.trim(),
                            meterReading = meterReading.trim(),
                            meterInstallationDate = meterInstallationDate.trim(),
                            regulatorDetails = regulatorDetails.trim(),
                            leakTestStatus = leakTestStatus.trim(),
                            remarks = remarks.trim(),
                            employeeName = "Rajesh Sharma",
                            jmrPhotoUri = jmrPhotoUriString,
                            meterPhotoUri = meterPhotoUriString
                        )
                        viewModel.saveGasIn(record) {
                            Toast.makeText(context, "Gas-in Commissioned Successfully!", Toast.LENGTH_SHORT).show()
                            bpNumber = ""
                            customerName = ""
                            jmrPhotoUriString = null
                            meterPhotoUriString = null
                            showConfirmationDialog = false
                            selectedTab = 1
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Authorize & Commission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Record Confirmation Dialog
    recordToDelete?.let { g ->
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            title = { Text("Delete Commissioning Record?") },
            text = { Text("Are you sure you want to delete Gas-in commissioning record for ${g.customerName} (BP: ${g.bpNumber})?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGasIn(g)
                        recordToDelete = null
                        Toast.makeText(context, "Gas-in record deleted", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
