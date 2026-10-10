package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customer: CustomerEntity,
    viewModel: GailGasViewModel,
    onNavigateBack: () -> Unit,
    onEditCustomer: (CustomerEntity) -> Unit
) {
    val context = LocalContext.current
    val allVisits by viewModel.allVisits.collectAsState()
    val customerVisits = allVisits.filter { it.customerBpNumber == customer.bpNumber }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer & JMR Record", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { onEditCustomer(customer) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Customer", tint = Color.White)
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
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = GailNavy.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("BP Number", customer.bpNumber)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied BP: ${customer.bpNumber}", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(customer.bpNumber, fontWeight = FontWeight.Bold, color = GailNavy)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp), tint = GailNavy)
                            }
                        }

                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Feasible",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(customer.customerName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("H.No: ${customer.houseNumber} • City: ${customer.city}", fontSize = 13.sp, color = GailOrange, fontWeight = FontWeight.SemiBold)
                    Text(customer.address, fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { FileMediaHelper.makePhoneCall(context, customer.mobileNumber) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Call ${customer.mobileNumber}", fontSize = 13.sp)
                    }
                }
            }

            // JMR Photo Preview if available
            if (!customer.jmrPhotoUri.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ATTACHED JMR CARD", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = customer.jmrPhotoUri,
                                contentDescription = "JMR Card Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            // Connection Specifications
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("JMR & METER SPECIFICATIONS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)
                    DetailRow("House Number", customer.houseNumber)
                    DetailRow("City", customer.city)
                    DetailRow("Pincode", customer.pincode)
                    DetailRow("Category", customer.requestType)
                    DetailRow("Connection Type", customer.connectionType)
                    if (customer.meterNumber.isNotBlank()) {
                        DetailRow("Meter No.", customer.meterNumber)
                        DetailRow("Model / Make", "${customer.meterModel} (${customer.meterMake})")
                        DetailRow("Initial Reading", customer.meterReading)
                        DetailRow("Burners", customer.burnersCount)
                        DetailRow("Extra Pipe", customer.extraPipe)
                    }
                    if (customer.remarks.isNotBlank()) {
                        DetailRow("Remarks", customer.remarks)
                    }
                }
            }

            // Customer Visit History
            if (customerVisits.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, tint = GailNavy)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("VISIT HISTORY (${customerVisits.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        customerVisits.forEach { v ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text("${v.visitDate} ${v.visitTime} • ${v.visitPurpose}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GailOrange)
                                Text("Performed: ${v.workPerformed}", fontSize = 11.sp, color = Color.DarkGray)
                                Divider(modifier = Modifier.padding(top = 6.dp), color = Color(0xFFE2E8F0))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    }
}
