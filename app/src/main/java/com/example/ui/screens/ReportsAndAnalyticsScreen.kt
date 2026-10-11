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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
                Text("EXCEL & PDF EXPORT HUB", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Dedicated Module Spreadsheets, Official PDFs & Field Audits", color = Color(0xFFCBD5E1), fontSize = 12.sp)
            }
        }

        // Summary Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("FIELD OPERATIONS DATA METRICS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                StatLine("Total Customers in Database", "${customers.size}")
                StatLine("House Visits / Site Surveys", "${visits.size}")
                StatLine("Feasibility Surveys Completed", "${feasibilities.size}")
                StatLine("Gas-In & Meter Commissioning Live", "${gasInRecords.size}")
                StatLine("TNF Escalation Cases", "${tnfCases.size}")
            }
        }

        // Dedicated Module-Wise Downloads Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("SEPARATE EXCEL & PDF DOWNLOADS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)
                Text("Alag-alag module ki real Excel sheets aur certified PDF documents download aur share karein:", fontSize = 12.sp, color = Color.Gray)

                // 1. Feasibility Module Exports
                ExportModuleRow(
                    title = "1. Feasibility Surveys",
                    subtitle = "${feasibilities.size} records",
                    icon = Icons.Default.Assignment,
                    onExportExcel = {
                        val file = ExportUtil.exportFeasibilityToExcel(context, feasibilities)
                        ExportUtil.shareFile(context, file, "text/csv", "GAIL Feasibility Excel Sheet")
                        Toast.makeText(context, "Exported Feasibility Excel", Toast.LENGTH_SHORT).show()
                    },
                    onExportPdf = {
                        val file = ExportUtil.exportFeasibilityToPdf(context, feasibilities)
                        ExportUtil.shareFile(context, file, "application/pdf", "GAIL Feasibility PDF Report")
                        Toast.makeText(context, "Exported Feasibility PDF", Toast.LENGTH_SHORT).show()
                    }
                )

                Divider(color = Color(0xFFE2E8F0))

                // 2. Survey / Daily Visits Module Exports
                ExportModuleRow(
                    title = "2. Survey & House Visits",
                    subtitle = "${visits.size} visits",
                    icon = Icons.Default.LocationOn,
                    onExportExcel = {
                        val file = ExportUtil.exportSiteVisitsToExcel(context, visits)
                        ExportUtil.shareFile(context, file, "text/csv", "GAIL Field Visits Excel Sheet")
                        Toast.makeText(context, "Exported Visits Excel", Toast.LENGTH_SHORT).show()
                    },
                    onExportPdf = {
                        val file = ExportUtil.exportSiteVisitsToPdf(context, visits)
                        ExportUtil.shareFile(context, file, "application/pdf", "GAIL Field Visits PDF Report")
                        Toast.makeText(context, "Exported Visits PDF", Toast.LENGTH_SHORT).show()
                    }
                )

                Divider(color = Color(0xFFE2E8F0))

                // 3. Gas-In & JMR Module Exports
                ExportModuleRow(
                    title = "3. Gas-In & JMR Commissioning",
                    subtitle = "${gasInRecords.size} live meters",
                    icon = Icons.Default.LocalGasStation,
                    onExportExcel = {
                        val file = ExportUtil.exportGasInToExcel(context, gasInRecords)
                        ExportUtil.shareFile(context, file, "text/csv", "GAIL Gas-In JMR Commissioning Excel")
                        Toast.makeText(context, "Exported Gas-In Excel", Toast.LENGTH_SHORT).show()
                    },
                    onExportPdf = {
                        val file = ExportUtil.exportGasInToPdf(context, gasInRecords)
                        ExportUtil.shareFile(context, file, "application/pdf", "GAIL Gas-In JMR PDF Report")
                        Toast.makeText(context, "Exported Gas-In PDF", Toast.LENGTH_SHORT).show()
                    }
                )

                Divider(color = Color(0xFFE2E8F0))

                // 4. Complete Customer Directory Exports
                ExportModuleRow(
                    title = "4. Customer Directory Master",
                    subtitle = "${customers.size} BP accounts",
                    icon = Icons.Default.People,
                    onExportExcel = {
                        val file = ExportUtil.exportCustomersToExcel(context, customers)
                        ExportUtil.shareFile(context, file, "text/csv", "GAIL Full Customer Master Excel")
                        Toast.makeText(context, "Exported Customer Master Excel", Toast.LENGTH_SHORT).show()
                    },
                    onExportPdf = {
                        val file = ExportUtil.exportComprehensiveSummaryPdf(context, customers, feasibilities, gasInRecords)
                        ExportUtil.shareFile(context, file, "application/pdf", "GAIL Operations Summary PDF")
                        Toast.makeText(context, "Exported Operations PDF", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Comprehensive All-In-One Field PDF
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("ALL-IN-ONE COMPREHENSIVE PDF REPORT", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)
                Text("Customers, feasibility surveys, aur gas-in commissioning sabhi ka combined executive document generate karein.", fontSize = 12.sp, color = Color.Gray)

                Button(
                    onClick = {
                        val pdfFile = ExportUtil.exportComprehensiveSummaryPdf(
                            context = context,
                            customers = customers,
                            feasibilities = feasibilities,
                            gasInRecords = gasInRecords
                        )
                        ExportUtil.shareFile(context, pdfFile, "application/pdf", "GAIL PMC Comprehensive All-In-One PDF Report")
                        Toast.makeText(context, "Exported All-In-One Operations PDF", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download & Share All-in-One Operations PDF")
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ExportModuleRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onExportExcel: () -> Unit,
    onExportPdf: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = GailNavy, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 11.sp, color = Color.Gray)
            }

            // Export Excel button
            Button(
                onClick = onExportExcel,
                colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Excel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Export PDF button
            Button(
                onClick = onExportPdf,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
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
}
