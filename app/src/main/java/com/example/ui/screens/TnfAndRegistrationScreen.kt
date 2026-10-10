package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.model.RegistrationStatus
import com.example.model.TnfEntity
import com.example.model.TnfStatus
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailOrange
import com.example.viewmodel.GailGasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TnfAndRegistrationScreen(
    viewModel: GailGasViewModel
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = TNF Cases, 1 = Registration

    val tnfCases by viewModel.allTnfCases.collectAsState()
    val customers by viewModel.allCustomers.collectAsState()
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    var showCreateTnfDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = GailNavy, tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text("PMC OPERATIONS & ESCALATION", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Manage Technical Non-Feasibility (TNF) & Customer Registrations", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = GailOrange
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("TNF Cases (${tnfCases.size})", color = if (selectedTab == 0) GailOrange else Color.White) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Registrations (${customers.size})", color = if (selectedTab == 1) GailOrange else Color.White) }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // TNF Section
            Box(modifier = Modifier.fillMaxSize()) {
                if (tnfCases.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No Technical Non-Feasible cases logged.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(tnfCases) { tnf ->
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
                                        Text(tnf.caseNumber, fontWeight = FontWeight.Bold, color = GailNavy, fontSize = 14.sp)
                                        Surface(
                                            color = if (tnf.status == "OPEN") Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = tnf.status,
                                                color = if (tnf.status == "OPEN") Color(0xFFC62828) else Color(0xFF2E7D32),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${tnf.customerName} (BP: ${tnf.bpNumber})",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                    Text(text = "Category: ${tnf.category}", color = GailOrange, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text(text = "Address: ${tnf.address}", color = Color.Gray, fontSize = 12.sp)

                                    if (tnf.remarks.isNotBlank()) {
                                        Text(text = "Remarks: ${tnf.remarks}", fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp))
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        if (tnf.status == "OPEN") {
                                            Button(
                                                onClick = {
                                                    viewModel.saveTnf(tnf.copy(status = "CLOSED", closedAt = System.currentTimeMillis())) {
                                                        Toast.makeText(context, "TNF case marked CLOSED & resolved", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Mark Resolved", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { showCreateTnfDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 80.dp, end = 20.dp),
                    containerColor = GailOrange,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Log TNF")
                }
            }
        } else {
            // Registration Module Section
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(customers) { cust ->
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
                                Text(cust.bpNumber, fontWeight = FontWeight.Bold, color = GailNavy, fontSize = 13.sp)
                                Surface(
                                    color = if (cust.registrationStatus == "COMPLETED") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = cust.registrationStatus,
                                        color = if (cust.registrationStatus == "COMPLETED") Color(0xFF2E7D32) else Color(0xFFE65100),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(cust.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("${cust.houseNumber}, ${cust.address}", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (cust.registrationStatus != "COMPLETED") {
                                    Button(
                                        onClick = {
                                            viewModel.saveCustomer(cust.copy(registrationStatus = "COMPLETED")) {
                                                Toast.makeText(context, "Registration status set to COMPLETED", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Complete Registration", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create TNF Dialog
    if (showCreateTnfDialog) {
        var bpNumber by remember { mutableStateOf("") }
        var customerName by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Customer Locked / Non-Availability") }
        var remarks by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateTnfDialog = false },
            title = { Text("Log New Technical Non-Feasibility (TNF)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bpNumber,
                        onValueChange = { bpNumber = it },
                        label = { Text("BP Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("TNF Category (dispute, locked, permission)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text("Technical Reason & Next Followup") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (bpNumber.isNotBlank() && customerName.isNotBlank()) {
                            val newTnf = TnfEntity(
                                caseNumber = "TNF-2026-" + (100..999).random(),
                                bpNumber = bpNumber.trim(),
                                customerName = customerName.trim(),
                                mobileNumber = "",
                                address = "Site Inspection Location",
                                project = "Bhopal CGD Project",
                                category = category,
                                assignedEmployee = "Rajesh Sharma",
                                status = "OPEN",
                                remarks = remarks.trim(),
                                followUpDate = today
                            )
                            viewModel.saveTnf(newTnf) {
                                Toast.makeText(context, "TNF case escalated and logged", Toast.LENGTH_SHORT).show()
                                showCreateTnfDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GailOrange)
                ) {
                    Text("Submit TNF")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTnfDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
