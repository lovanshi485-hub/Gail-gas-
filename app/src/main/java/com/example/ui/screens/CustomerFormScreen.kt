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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.CustomerEntity
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailOrange
import com.example.util.FileMediaHelper
import com.example.viewmodel.GailGasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFormScreen(
    viewModel: GailGasViewModel,
    existingCustomer: CustomerEntity? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    // Simplified fields matching JMR format & user requirements
    var bpNumber by remember { mutableStateOf(existingCustomer?.bpNumber ?: "34000" + (10000..99999).random()) }
    var customerName by remember { mutableStateOf(existingCustomer?.customerName ?: "") }
    var mobileNumber by remember { mutableStateOf(existingCustomer?.mobileNumber ?: "") }
    var houseNumber by remember { mutableStateOf(existingCustomer?.houseNumber ?: "") }
    var city by remember { mutableStateOf(existingCustomer?.city ?: "Mandideep") }
    var address by remember { mutableStateOf(existingCustomer?.address ?: "") }
    var pincode by remember { mutableStateOf(existingCustomer?.pincode ?: "462046") }
    var remarks by remember { mutableStateOf(existingCustomer?.remarks ?: "") }

    // Optional Meter / JMR Quick Fields
    var meterNumber by remember { mutableStateOf(existingCustomer?.meterNumber ?: "") }
    var meterMake by remember { mutableStateOf(existingCustomer?.meterMake ?: "Raychem RPG") }
    var meterReading by remember { mutableStateOf(existingCustomer?.meterReading ?: "0.000") }

    // JMR Photo Upload
    var jmrPhotoUriString by remember { mutableStateOf(existingCustomer?.jmrPhotoUri) }
    var cameraTargetUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraTargetUri != null) {
            jmrPhotoUriString = cameraTargetUri.toString()
            Toast.makeText(context, "JMR Photo Attached", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val copied = FileMediaHelper.copyUriToAppStorage(context, it)
            jmrPhotoUriString = copied ?: it.toString()
            Toast.makeText(context, "JMR Photo Selected", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingCustomer == null) "Add Customer / JMR" else "Edit Customer Record", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GailNavy,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Main Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("CUSTOMER IDENTIFICATION", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                    OutlinedTextField(
                        value = bpNumber,
                        onValueChange = { bpNumber = it },
                        label = { Text("Business Partner (BP) No. *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Name of Consumer *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text("Mobile Number *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = houseNumber,
                            onValueChange = { houseNumber = it },
                            label = { Text("H. No. * (e.g. FF-01)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City * (e.g. Mandideep)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address / Premise (e.g. Indus Realty Narmada)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = pincode,
                            onValueChange = { pincode = it },
                            label = { Text("Pincode") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = { Text("Remarks") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Meter Details Card (Optional JMR fields)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("METER / JMR DETAILS (OPTIONAL)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                    OutlinedTextField(
                        value = meterNumber,
                        onValueChange = { meterNumber = it },
                        label = { Text("Meter No. (e.g. 21241466)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = meterMake,
                            onValueChange = { meterMake = it },
                            label = { Text("Meter Make (Raychem RPG)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = meterReading,
                            onValueChange = { meterReading = it },
                            label = { Text("Initial Reading (0.022)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // JMR Photo Upload Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("UPLOAD JMR CARD PHOTO", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)
                    Text("Capture or attach GAIL Gas JMR slip photo", fontSize = 12.sp, color = Color.Gray)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = {
                                val pair = FileMediaHelper.createMediaFile(context, isVideo = false)
                                cameraTargetUri = pair.second
                                cameraLauncher.launch(pair.second)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Camera", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                galleryLauncher.launch("image/*")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gallery", fontSize = 12.sp)
                        }
                    }

                    jmrPhotoUriString?.let { uriStr ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
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
                                    .padding(6.dp)
                                    .size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            // Save Button (Kuch bhi mandatory nahi hai)
            Button(
                onClick = {
                    val safeBp = if (bpNumber.isBlank()) "BP" + (100000..999999).random() else bpNumber.trim()
                    val safeName = if (customerName.isBlank()) "Consumer-${safeBp.takeLast(4)}" else customerName.trim()

                    val customer = CustomerEntity(
                        id = existingCustomer?.id ?: 0L,
                        date = existingCustomer?.date ?: today,
                        bpNumber = safeBp,
                        customerName = safeName,
                        mobileNumber = mobileNumber.trim(),
                        houseNumber = houseNumber.trim(),
                        city = city.trim(),
                        address = address.trim(),
                        pincode = pincode.trim(),
                        remarks = remarks.trim(),
                        project = "GAIL Gas $city CGD",
                        meterNumber = meterNumber.trim(),
                        meterMake = meterMake.trim(),
                        meterReading = meterReading.trim(),
                        jmrPhotoUri = jmrPhotoUriString,
                        registrationStatus = "COMPLETED",
                        feasibilityStatus = "FEASIBLE"
                    )
                    viewModel.saveCustomer(customer) {
                        Toast.makeText(context, "Customer & JMR record saved", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
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
                Text("Save Customer Record", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
