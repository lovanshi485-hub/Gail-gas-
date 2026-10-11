package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.model.FeasibilityEntity
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailOrange
import com.example.util.ExportUtil
import com.example.util.FileMediaHelper
import com.example.viewmodel.GailGasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FeasibilityScreen(
    viewModel: GailGasViewModel
) {
    val context = LocalContext.current
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val feasibilities by viewModel.allFeasibilities.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = New Entry, 1 = Past Surveys

    // Non-mandatory fields: BP number, Name, Mobile, H.No, City, Address, Remarks
    var bpNumber by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Mandideep") }
    var address by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    // Multi-Select Status list (User can choose ANY count of statuses)
    val availableStatuses = listOf("Feasible", "TNF", "GI Pending", "GC Pending", "GI Done", "GC Done")
    val selectedStatuses = remember { mutableStateListOf("Feasible") }

    // Media (Both Photo & Video are optional / non-mandatory)
    var photoUriString by remember { mutableStateOf<String?>(null) }
    var videoUriString by remember { mutableStateOf<String?>(null) }
    var cameraTargetUri by remember { mutableStateOf<Uri?>(null) }
    var isCapturingVideo by remember { mutableStateOf(false) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraTargetUri != null) {
            photoUriString = cameraTargetUri.toString()
            Toast.makeText(context, "Survey Photo Attached (Optional)", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val copied = FileMediaHelper.copyUriToAppStorage(context, it)
            photoUriString = copied ?: it.toString()
            Toast.makeText(context, "Gallery Photo Attached", Toast.LENGTH_SHORT).show()
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            videoUriString = it.toString()
            Toast.makeText(context, "Survey Video Attached (Optional)", Toast.LENGTH_SHORT).show()
        }
    }

    var feasibilityToDelete by remember { mutableStateOf<FeasibilityEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Simple Top Header with Direct Excel Export
        Surface(color = GailNavy, tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FEASIBILITY & SURVEY MODULE",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Multi-Status Selection • Media Optional • Zero Mandatory Fields",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                    }

                    // Direct Feasibility Excel & PDF Export Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                val file = ExportUtil.exportFeasibilityToExcel(context, feasibilities)
                                ExportUtil.shareFile(context, file, "text/csv", "GAIL Feasibility Excel Sheet")
                                Toast.makeText(context, "Feasibility Excel sheet exported", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Excel", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val file = ExportUtil.exportFeasibilityToPdf(context, feasibilities)
                                ExportUtil.shareFile(context, file, "application/pdf", "GAIL Feasibility PDF Report")
                                Toast.makeText(context, "Feasibility PDF report exported", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("PDF", fontSize = 11.sp)
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
                        text = { Text("New Survey", color = if (selectedTab == 0) GailOrange else Color.White) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("All Surveys (${feasibilities.size})", color = if (selectedTab == 1) GailOrange else Color.White) }
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
                // Customer & Location Card
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
                            text = "LOCATION & CONSUMER INFO (KUCH BHI MANDATORY NAHI HAI)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GailNavy
                        )

                        OutlinedTextField(
                            value = bpNumber,
                            onValueChange = { bpNumber = it },
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

                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = { mobileNumber = it },
                            label = { Text("Mobile Number (Optional)") },
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
                                label = { Text("H. No. (Optional)") },
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
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address / Colony (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // MULTI-SELECT STATUS CARD (Choose any number of statuses)
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
                            text = "STATUS CHOOSE KAREIN (EK SE JYADA BHI CHUN SAKTE HAIN)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GailNavy
                        )
                        Text(
                            text = "Jo bhi status applicable ho, tap karke select karein:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        // 6 status chips in 2x3 grid
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                MultiStatusChip(
                                    label = "Feasible",
                                    color = Color(0xFF2E7D32),
                                    isSelected = selectedStatuses.contains("Feasible"),
                                    onToggle = {
                                        if (selectedStatuses.contains("Feasible")) selectedStatuses.remove("Feasible")
                                        else selectedStatuses.add("Feasible")
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                MultiStatusChip(
                                    label = "TNF",
                                    color = Color(0xFFC62828),
                                    isSelected = selectedStatuses.contains("TNF"),
                                    onToggle = {
                                        if (selectedStatuses.contains("TNF")) selectedStatuses.remove("TNF")
                                        else selectedStatuses.add("TNF")
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                MultiStatusChip(
                                    label = "GI Pending",
                                    color = Color(0xFFE65100),
                                    isSelected = selectedStatuses.contains("GI Pending"),
                                    onToggle = {
                                        if (selectedStatuses.contains("GI Pending")) selectedStatuses.remove("GI Pending")
                                        else selectedStatuses.add("GI Pending")
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                MultiStatusChip(
                                    label = "GC Pending",
                                    color = Color(0xFFF57C00),
                                    isSelected = selectedStatuses.contains("GC Pending"),
                                    onToggle = {
                                        if (selectedStatuses.contains("GC Pending")) selectedStatuses.remove("GC Pending")
                                        else selectedStatuses.add("GC Pending")
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                MultiStatusChip(
                                    label = "GI Done",
                                    color = Color(0xFF1565C0),
                                    isSelected = selectedStatuses.contains("GI Done"),
                                    onToggle = {
                                        if (selectedStatuses.contains("GI Done")) selectedStatuses.remove("GI Done")
                                        else selectedStatuses.add("GI Done")
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                MultiStatusChip(
                                    label = "GC Done",
                                    color = Color(0xFF00796B),
                                    isSelected = selectedStatuses.contains("GC Done"),
                                    onToggle = {
                                        if (selectedStatuses.contains("GC Done")) selectedStatuses.remove("GC Done")
                                        else selectedStatuses.add("GC Done")
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        if (selectedStatuses.isNotEmpty()) {
                            Text(
                                text = "Selected: ${selectedStatuses.joinToString(", ")}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GailOrange
                            )
                        }

                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = { Text("Remark / Visited Notes (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // OPTIONAL PHOTO & VIDEO ATTACHMENT CARD
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
                            text = "PHOTO & VIDEO (OPTIONAL - MANDATORY NAHI HAI)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GailNavy
                        )
                        Text(
                            text = "Aap chahein to site photo ya video attach kar sakte hain, bina media ke bhi save hoga.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val pair = FileMediaHelper.createMediaFile(context, isVideo = false)
                                    cameraTargetUri = pair.second
                                    photoLauncher.launch(pair.second)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Camera", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gallery", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { videoPickerLauncher.launch("video/*") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Video", fontSize = 11.sp)
                            }
                        }

                        // Previews
                        photoUriString?.let { uri ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, GailOrange, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Photo Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { photoUriString = null },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(26.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Delete Photo", tint = Color.Red)
                                }
                            }
                        }

                        videoUriString?.let {
                            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Video Attached Ready", fontSize = 11.sp, color = Color(0xFF2E7D32), modifier = Modifier.weight(1f))
                                    IconButton(onClick = { videoUriString = null }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove Video", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }

                // Save Survey Button (Zero mandatory constraint)
                Button(
                    onClick = {
                        val statusCombined = if (selectedStatuses.isEmpty()) "Feasible" else selectedStatuses.joinToString(", ")
                        val safeBp = if (bpNumber.isBlank()) "BP" + (100000..999999).random() else bpNumber.trim()
                        val safeName = if (customerName.isBlank()) "Consumer-${safeBp.takeLast(4)}" else customerName.trim()

                        val entity = FeasibilityEntity(
                            bpNumber = safeBp,
                            customerName = safeName,
                            mobileNumber = mobileNumber.trim(),
                            houseNumber = houseNumber.trim(),
                            address = address.trim(),
                            city = city.trim(),
                            project = "GAIL Gas $city CGD",
                            visitDate = today,
                            status = statusCombined,
                            remarks = remarks.trim(),
                            photoUri = photoUriString,
                            videoUri = videoUriString
                        )
                        viewModel.saveFeasibility(entity) {
                            Toast.makeText(context, "Feasibility survey saved successfully!", Toast.LENGTH_SHORT).show()
                            bpNumber = ""
                            customerName = ""
                            mobileNumber = ""
                            houseNumber = ""
                            address = ""
                            remarks = ""
                            photoUriString = null
                            videoUriString = null
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
                    Text("Save Feasibility Record", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        } else {
            // Past Surveys Tab with DELETE and Export
            Column(modifier = Modifier.fillMaxSize()) {
                if (feasibilities.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No feasibility surveys recorded yet.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(feasibilities, key = { it.id }) { f ->
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
                                            text = if (f.bpNumber.isNotBlank()) "BP: ${f.bpNumber}" else "Site Survey #${f.id}",
                                            fontWeight = FontWeight.Bold,
                                            color = GailNavy,
                                            fontSize = 14.sp
                                        )
                                        Surface(
                                            color = GailOrange.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = f.status,
                                                color = GailOrange,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(f.customerName.ifBlank { "Unassigned Consumer" }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("H.No: ${f.houseNumber.ifBlank { "N/A" }} • City: ${f.city.ifBlank { "Mandideep" }}", fontSize = 12.sp, color = GailNavy, fontWeight = FontWeight.Medium)
                                    if (f.address.isNotBlank()) {
                                        Text(f.address, fontSize = 12.sp, color = Color.Gray)
                                    }
                                    if (f.remarks.isNotBlank()) {
                                        Text("Remarks: ${f.remarks}", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { feasibilityToDelete = f },
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

    // Delete confirmation dialog
    feasibilityToDelete?.let { f ->
        AlertDialog(
            onDismissRequest = { feasibilityToDelete = null },
            title = { Text("Delete Feasibility Survey?") },
            text = { Text("Are you sure you want to permanently delete survey for ${f.customerName} (BP: ${f.bpNumber})?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFeasibility(f)
                        feasibilityToDelete = null
                        Toast.makeText(context, "Feasibility survey deleted", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { feasibilityToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MultiStatusChip(
    label: String,
    color: Color,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onToggle,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) color else color.copy(alpha = 0.08f),
        contentColor = if (isSelected) Color.White else color,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}
