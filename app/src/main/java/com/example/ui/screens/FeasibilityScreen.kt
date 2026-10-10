package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.model.FeasibilityEntity
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailOrange
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

    // Fields: BP, Name, Mobile, H.No, City, Address, Remarks, Status
    var bpNumber by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Mandideep") }
    var address by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    // User-requested exact status options:
    // "Feasible", "TNF", "GI Pending", "GC Pending", "GI Done", "GC Done"
    val statusOptions = listOf("Feasible", "TNF", "GI Pending", "GC Pending", "GI Done", "GC Done")
    var selectedStatus by remember { mutableStateOf("Feasible") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Simple Top Header
        Surface(color = GailNavy, tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text(
                    text = "FEASIBILITY STATUS & SURVEY",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Feasible • TNF • GI Pending • GC Pending • GI Done • GC Done",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp
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
                        text = { Text("New Feasibility", color = if (selectedTab == 0) GailOrange else Color.White) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Surveys (${feasibilities.size})", color = if (selectedTab == 1) GailOrange else Color.White) }
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "CUSTOMER & LOCATION DETAILS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = GailNavy
                        )

                        OutlinedTextField(
                            value = bpNumber,
                            onValueChange = { bpNumber = it },
                            label = { Text("BP Number * (e.g. 3400022314)") },
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

                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = { mobileNumber = it },
                            label = { Text("Mobile Number (e.g. 9910921088)") },
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
                            label = { Text("Address / Society (e.g. Indus Realty Narmada)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Status Selection Card (Feasible, TNF, GI Pending, GC Pending, GI Done, GC Done)
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
                            text = "STATUS CHOOSE KAREIN (ONE-TAP)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = GailNavy
                        )
                        Text(
                            text = "Aasani se status select karein:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        // 6 status options in easy-to-tap grid/chips
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatusOptionButton(
                                    label = "Feasible",
                                    color = Color(0xFF2E7D32),
                                    isSelected = selectedStatus == "Feasible",
                                    onClick = { selectedStatus = "Feasible" },
                                    modifier = Modifier.weight(1f)
                                )
                                StatusOptionButton(
                                    label = "TNF",
                                    color = Color(0xFFC62828),
                                    isSelected = selectedStatus == "TNF",
                                    onClick = { selectedStatus = "TNF" },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatusOptionButton(
                                    label = "GI Pending",
                                    color = Color(0xFFE65100),
                                    isSelected = selectedStatus == "GI Pending",
                                    onClick = { selectedStatus = "GI Pending" },
                                    modifier = Modifier.weight(1f)
                                )
                                StatusOptionButton(
                                    label = "GC Pending",
                                    color = Color(0xFFF57C00),
                                    isSelected = selectedStatus == "GC Pending",
                                    onClick = { selectedStatus = "GC Pending" },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatusOptionButton(
                                    label = "GI Done",
                                    color = Color(0xFF1565C0),
                                    isSelected = selectedStatus == "GI Done",
                                    onClick = { selectedStatus = "GI Done" },
                                    modifier = Modifier.weight(1f)
                                )
                                StatusOptionButton(
                                    label = "GC Done",
                                    color = Color(0xFF00796B),
                                    isSelected = selectedStatus == "GC Done",
                                    onClick = { selectedStatus = "GC Done" },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = { Text("Remark (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Submit Button
                Button(
                    onClick = {
                        if (bpNumber.isBlank() || customerName.isBlank()) {
                            Toast.makeText(context, "BP Number aur Customer Name likhna zaroori hai", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val entity = FeasibilityEntity(
                            bpNumber = bpNumber.trim(),
                            customerName = customerName.trim(),
                            mobileNumber = mobileNumber.trim(),
                            houseNumber = houseNumber.trim(),
                            address = address.trim(),
                            city = city.trim(),
                            project = "GAIL Gas $city CGD",
                            visitDate = today,
                            status = selectedStatus,
                            remarks = remarks.trim()
                        )
                        viewModel.saveFeasibility(entity) {
                            Toast.makeText(context, "Feasibility: $selectedStatus save ho gaya!", Toast.LENGTH_SHORT).show()
                            bpNumber = ""
                            customerName = ""
                            mobileNumber = ""
                            houseNumber = ""
                            address = ""
                            remarks = ""
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
            // Past Surveys Tab
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
                    items(feasibilities) { f ->
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
                                        text = "BP: ${f.bpNumber}",
                                        fontWeight = FontWeight.Bold,
                                        color = GailNavy,
                                        fontSize = 14.sp
                                    )
                                    val statusColor = when (f.status) {
                                        "Feasible" -> Color(0xFF2E7D32)
                                        "TNF" -> Color(0xFFC62828)
                                        "GI Done" -> Color(0xFF1565C0)
                                        "GC Done" -> Color(0xFF00796B)
                                        else -> Color(0xFFE65100)
                                    }
                                    Surface(
                                        color = statusColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = f.status,
                                            color = statusColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(f.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("H.No: ${f.houseNumber.ifBlank { "N/A" }} • City: ${f.city.ifBlank { "Mandideep" }}", fontSize = 12.sp, color = GailOrange, fontWeight = FontWeight.Medium)
                                if (f.address.isNotBlank()) {
                                    Text(f.address, fontSize = 12.sp, color = Color.Gray)
                                }
                                if (f.remarks.isNotBlank()) {
                                    Text("Remark: ${f.remarks}", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusOptionButton(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) color else color.copy(alpha = 0.1f),
        contentColor = if (isSelected) Color.White else color,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
