package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DprEntity
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailOrange
import com.example.util.ExportUtil
import com.example.viewmodel.GailGasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DprEditorScreen(
    viewModel: GailGasViewModel
) {
    val context = LocalContext.current
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    val customers by viewModel.allCustomers.collectAsState()
    val visits by viewModel.allVisits.collectAsState()
    val feasibilities by viewModel.allFeasibilities.collectAsState()
    val gasInRecords by viewModel.allGasInRecords.collectAsState()
    val tnfCases by viewModel.allTnfCases.collectAsState()
    val dprReports by viewModel.allDprReports.collectAsState()

    var reportDate by remember { mutableStateOf(today) }
    var reportTitle by remember { mutableStateOf("GAIL Gas PMC Field Progress - Bhopal CGD Operations") }
    var project by remember { mutableStateOf("Bhopal CGD Project") }
    var preparedBy by remember { mutableStateOf("Rajesh Sharma") }

    // Word-style content area
    var dprTextContent by remember {
        mutableStateOf(
            if (dprReports.isNotEmpty()) {
                dprReports.first().formattedContentHtml
            } else {
                """
1. EXECUTIVE PROGRESS SUMMARY:
Today PMC supervision teams conducted comprehensive field coverage across Arera Colony, Gulmohar Enclave, and Shahpura sectors.

2. KEY HIGHLIGHTS:
- Total site inspections: ${visits.size}
- Live gas-in completed: ${gasInRecords.size}
- Feasibility surveys cleared: ${feasibilities.size}
- TNF cases escalated to GAIL City Gas team: ${tnfCases.size}

3. TECHNICAL OBSERVATIONS:
- GI riser risers inspected for alignment and wall bracket rigidity.
- Gas pressure testing calibrated and maintained at standard 21 mbar.
- Zero customer gas leakages or safety complaints recorded.
                """.trimIndent()
            }
        )
    }

    val statsSummary = "Visits: ${visits.size} | Feasible: ${feasibilities.size} | Gas-in: ${gasInRecords.size} | TNF: ${tnfCases.size}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Document Header
        Surface(color = GailNavy, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("DAILY PROGRESS REPORT (DPR) EDITOR", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Word-Style Document Editor • PDF & DOCX Export • Auto Draft", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            // Auto populate DPR from today's live records
                            val generated = """
GAIL GAS PMC - AUTOMATED FIELD DPR
Date: $reportDate | Project: $project | Supervisor: $preparedBy

1. SUMMARY OF SITE ACTIVITIES:
- Total Houses Visited: ${visits.size}
- Unique Customers Inspected: ${visits.map { it.customerBpNumber }.distinct().size}
- Feasibility Surveys Conducted: ${feasibilities.size}
- Registrations Verified: ${customers.count { it.registrationStatus == "COMPLETED" }}
- Meters Commissioned (Gas-in): ${gasInRecords.size}
- TNF Cases Active: ${tnfCases.count { it.status == "OPEN" }}

2. GAS-IN COMMISSIONING DETAILS:
${gasInRecords.joinToString("\n") { "• BP: ${it.bpNumber} | Meter: ${it.meterNumber} (${it.meterMake}) | Leak Test: ${it.leakTestStatus}" }}

3. PENDING ACTION ITEMS & TNF ESCALATION:
${tnfCases.joinToString("\n") { "• [${it.caseNumber}] BP: ${it.bpNumber} - ${it.category}: ${it.remarks}" }}

4. SAFETY & COMPLIANCE:
- 100% PPE compliance verified.
- Fire extinguisher checked on support PMC utility vehicle.
                            """.trimIndent()
                            dprTextContent = generated
                            Toast.makeText(context, "DPR auto-drafted from today's records!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto-Draft from Work", fontSize = 12.sp)
                    }
                }
            }
        }

        // Metadata Fields
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = reportTitle,
                    onValueChange = { reportTitle = it },
                    label = { Text("Report Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = reportDate,
                        onValueChange = { reportDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = preparedBy,
                        onValueChange = { preparedBy = it },
                        label = { Text("Prepared By") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        }

        // Word-Style Formatting Toolbar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { dprTextContent += "\n**BOLD HEADING**\n" }) {
                        Icon(Icons.Default.FormatBold, contentDescription = "Bold", tint = GailNavy)
                    }
                    IconButton(onClick = { dprTextContent += " *italic emphasis* " }) {
                        Icon(Icons.Default.FormatItalic, contentDescription = "Italic", tint = GailNavy)
                    }
                    IconButton(onClick = { dprTextContent += " __underlined text__ " }) {
                        Icon(Icons.Default.FormatUnderlined, contentDescription = "Underline", tint = GailNavy)
                    }
                    IconButton(onClick = { dprTextContent += "\n• Bullet item: " }) {
                        Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullets", tint = GailNavy)
                    }
                    IconButton(onClick = {
                        dprTextContent += "\n| Section | Target | Actual | Status |\n| Feasibility | 10 | 8 | On Track |\n| Gas-in | 5 | 5 | 100% |\n"
                    }) {
                        Icon(Icons.Default.TableChart, contentDescription = "Table", tint = GailNavy)
                    }
                }
            }
        }

        // Large Editable Document Writing Canvas
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "OFFICIAL PMC DAILY PROGRESS DOCUMENT",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))

                OutlinedTextField(
                    value = dprTextContent,
                    onValueChange = { dprTextContent = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Export Actions (PDF, DOCX, Save Draft)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val entity = DprEntity(
                        reportDate = reportDate,
                        title = reportTitle,
                        project = project,
                        preparedBy = preparedBy,
                        formattedContentHtml = dprTextContent,
                        totalHousesVisited = visits.size,
                        uniqueCustomers = visits.map { it.customerBpNumber }.distinct().size,
                        feasibleCount = feasibilities.size,
                        nonFeasibleCount = 0,
                        registrationCount = customers.size,
                        gasInCount = gasInRecords.size,
                        tnfCount = tnfCases.size,
                        isDraft = false
                    )
                    viewModel.saveDpr(entity) {
                        Toast.makeText(context, "DPR document saved to database", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = GailNavy),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save DPR", fontSize = 11.sp)
            }

            Button(
                onClick = {
                    val pdfFile = ExportUtil.exportDprToPdf(
                        context,
                        reportDate,
                        project,
                        preparedBy,
                        dprTextContent,
                        statsSummary
                    )
                    ExportUtil.shareFile(context, pdfFile, "application/pdf", "GAIL PMC Daily Progress Report (PDF)")
                    Toast.makeText(context, "Generated Official PDF Document", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export PDF", fontSize = 11.sp)
            }

            Button(
                onClick = {
                    val docFile = ExportUtil.exportDprToDocx(
                        context,
                        reportDate,
                        project,
                        preparedBy,
                        dprTextContent
                    )
                    ExportUtil.shareFile(context, docFile, "application/msword", "GAIL PMC DPR (DOCX)")
                    Toast.makeText(context, "Generated Word DOCX Document", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export DOC", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
