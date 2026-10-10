package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailOrange
import com.example.util.ExportUtil
import com.example.viewmodel.GailGasViewModel

@Composable
fun ReportsAndAnalyticsScreen(
    viewModel: GailGasViewModel
) {
    val context = LocalContext.current
    val customers by viewModel.allCustomers.collectAsState()
    val visits by viewModel.allVisits.collectAsState()
    val feasibilities by viewModel.allFeasibilities.collectAsState()
    val gasInRecords by viewModel.allGasInRecords.collectAsState()
    val tnfCases by viewModel.allTnfCases.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            color = GailNavy,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("PMC REPORTS & ANALYTICS", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Comprehensive Field Audits, Progress Matrices & Exports", color = Color(0xFFCBD5E1), fontSize = 12.sp)
            }
        }

        // Summary Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("OPERATIONAL KPI SUMMARY", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                StatLine("Total Customers in Register", "${customers.size}")
                StatLine("Site Inspections & House Visits", "${visits.size}")
                StatLine("Feasible Surveys & JMR Completed", "${feasibilities.size}")
                StatLine("Completed Gas-In Meter Connections", "${gasInRecords.size}")
                StatLine("Document Registrations Completed", "${customers.count { it.registrationStatus == "COMPLETED" }}")
            }
        }

        // Export Report Options Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("EXPORT FIELD AUDIT REPORTS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                Button(
                    onClick = {
                        val file = ExportUtil.exportCustomersToExcel(context, customers)
                        ExportUtil.shareFile(context, file, "text/csv", "Full PMC Customer Audit Excel")
                        Toast.makeText(context, "Exported Complete Customer Ledger", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GailNavy),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Customer & Excel Matrix (.csv/.xlsx)")
                }

                Button(
                    onClick = {
                        val pdfFile = ExportUtil.exportDprToPdf(
                            context = context,
                            reportDate = "All_Time_Summary",
                            project = "Bhopal CGD Project",
                            preparedBy = "PMC Field Team",
                            content = "Complete audit of ${customers.size} customers, ${visits.size} visits, and ${gasInRecords.size} gas-in commissioning records.",
                            statsSummary = "Customers: ${customers.size} | Visits: ${visits.size} | Gas-in: ${gasInRecords.size}"
                        )
                        ExportUtil.shareFile(context, pdfFile, "application/pdf", "GAIL PMC Comprehensive PDF Audit")
                        Toast.makeText(context, "Exported Field Executive PDF Report", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Executive PDF Report")
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun StatLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Color.DarkGray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GailNavy)
    }
    Divider(color = Color(0xFFF1F5F9))
}
