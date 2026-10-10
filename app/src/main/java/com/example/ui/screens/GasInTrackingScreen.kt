package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraTargetUri != null) {
            if (capturingForJmr) {
                jmrPhotoUriString = cameraTargetUri.toString()
                Toast.makeText(context, "JMR Photo Attached (Optional)", Toast.LENGTH_SHORT).show()
            } else {
                meterPhotoUriString = cameraTargetUri.toString()
                Toast.makeText(context, "Meter Photo Attached (Optional)", Toast.LENGTH_SHORT).show()
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

    var showConfirmationDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
        Surface(
            color = GailNavy,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("GAS-IN & METER TRACKING", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Domestic Gas Commissioning • Optional JMR & Meter Photo", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Total Commissioned Live: ${gasInRecords.size}", color = GailOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Customer Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("TARGET CONNECTION (BP NUMBER)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                OutlinedTextField(
                    value = bpNumber,
                    onValueChange = { bpNumber = it },
                    label = { Text("Customer BP Number * (e.g. 3400022314)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name * (e.g. Devendra Kumar)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Metering Specs Card (Optional)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("METER DETAILS (OPTIONAL)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                OutlinedTextField(
                    value = meterNumber,
                    onValueChange = { meterNumber = it },
                    label = { Text("Meter Serial Number (e.g. 21241466)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = meterMake,
                        onValueChange = { meterMake = it },
                        label = { Text("Meter Make") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = meterReading,
                        onValueChange = { meterReading = it },
                        label = { Text("Initial Reading") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // JMR Photo & Meter Photo Uploads (Optional / Non-Mandatory)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("PHOTOS (OPTIONAL - MANDATORY NAHI HAI)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)
                Text("Aap chahe to JMR card ya Meter ki photo le sakte hain, bina photo ke bhi save kar sakte hain.", fontSize = 12.sp, color = Color.Gray)

                // 1. JMR Photo Section
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("1. JMR Slip Photo (Optional):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GailNavy)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(26.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
                            }
                        }
                    }
                }

                // 2. Meter Photo Section
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("2. Meter Reading Photo (Optional):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = GailNavy)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(26.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        // Commission Button
        Button(
            onClick = {
                if (bpNumber.isBlank() || customerName.isBlank()) {
                    Toast.makeText(context, "BP Number aur Customer Name likhna zaroori hai", Toast.LENGTH_SHORT).show()
                    return@Button
                }
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

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirm Gas-in Commissioning?") },
            text = { Text("BP $bpNumber ($customerName) ke liye Gas-in confirm karein. Photos optional hain.") },
            confirmButton = {
                Button(
                    onClick = {
                        val record = GasInEntity(
                            bpNumber = bpNumber.trim(),
                            customerName = customerName.trim(),
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
                            Toast.makeText(context, "Gas-in Commissioned Successfully!", Toast.LENGTH_LONG).show()
                            bpNumber = ""
                            customerName = ""
                            jmrPhotoUriString = null
                            meterPhotoUriString = null
                            showConfirmationDialog = false
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
}
