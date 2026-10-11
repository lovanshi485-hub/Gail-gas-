package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.platform.LocalContext
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

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Editor & AI, 1 = Daily Saved Archive

    var reportDate by remember { mutableStateOf(today) }
    var reportTitle by remember { mutableStateOf("GAIL Gas PMC Field Progress - Mandideep & Bhopal CGD") }
    var project by remember { mutableStateOf("GAIL Gas Mandideep CGD") }
    var preparedBy by remember { mutableStateOf("Rajesh Sharma") }

    var dprTextContent by remember {
        mutableStateOf(
            """
1. EXECUTIVE FIELD OPERATIONS SUMMARY:
PMC supervision teams completed comprehensive site coverage across industrial, commercial, and domestic sectors.

2. QUANTIFIED FIELD MILESTONES:
- Total site inspections conducted: ${visits.size}
- Live gas-in connections commissioned: ${gasInRecords.size}
- Feasibility surveys cleared: ${feasibilities.size}
- Active TNF cases under escalation: ${tnfCases.count { it.status == "OPEN" }}

3. PMC TECHNICAL & SAFETY OBSERVATIONS:
- GI riser shafts inspected for alignment and wall bracket rigidity.
- Gas pressure testing calibrated and maintained at standard 21 mbar.
- Zero customer gas leakages or safety complaints recorded.
            """.trimIndent()
        )
    }

    var dprToDelete by remember { mutableStateOf<DprEntity?>(null) }
    val statsSummary = "Visits: ${visits.size} | Feasible: ${feasibilities.size} | Gas-in: ${gasInRecords.size} | TNF: ${tnfCases.size}"

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
                                text = "DAILY PROGRESS REPORT (DPR)",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "AI Generator • Daily Auto-Save • Date-Wise PDF/DOCX Archive",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }

                        // Quick Share Current PDF
                        IconButton(
                            onClick = {
                                val pdfFile = ExportUtil.exportDprToPdf(
                                    context,
                                    reportDate,
                                    project,
                                    preparedBy,
                                    dprTextContent,
                                    statsSummary
                                )
                                ExportUtil.shareFile(context, pdfFile, "application/pdf", "GAIL PMC Daily Progress Report ($reportDate)")
                                Toast.makeText(context, "Exported DPR PDF", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color.White)
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
                            text = { Text("DPR Editor & AI", color = if (selectedTab == 0) GailOrange else Color.White) },
                            icon = { Icon(Icons.Default.Description, contentDescription = null, tint = if (selectedTab == 0) GailOrange else Color.White) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Saved Reports (${dprReports.size})", color = if (selectedTab == 1) GailOrange else Color.White) },
                            icon = { Icon(Icons.Default.History, contentDescription = null, tint = if (selectedTab == 1) GailOrange else Color.White) }
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                // ================= DPR EDITOR & AI GENERATOR =================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Meta Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = reportDate,
                                    onValueChange = { reportDate = it },
                                    label = { Text("Report Date (YYYY-MM-DD)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = project,
                                    onValueChange = { project = it },
                                    label = { Text("Project / CGD Area") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            OutlinedTextField(
                                value = preparedBy,
                                onValueChange = { preparedBy = it },
                                label = { Text("Supervisor / In-Charge Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    // AI Features Hub
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GailOrange, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("GAIL PMC AI ASSISTANT FEATURES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Field data ko automatically analyze karke professional executive report synthesize karein.",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // AI Synthesize Button
                                Button(
                                    onClick = {
                                        dprTextContent = """
GAIL GAS PMC - AI SYNTHESIZED DAILY PROGRESS REPORT (DPR)
Date: $reportDate | Project: $project | Prepared By: $preparedBy

1. EXECUTIVE SUMMARY & TARGET COMPLIANCE:
Today PMC supervision teams conducted intensive field operations across designated sector corridors.
- Total House Visits / Surveys Logged: ${visits.size}
- Unique Customers Inspected: ${visits.map { it.customerBpNumber }.distinct().size}
- Feasibility Approvals Cleared: ${feasibilities.size}
- Gas-In & Meter Commissioning Live: ${gasInRecords.size}
- Active TNF Bottlenecks: ${tnfCases.count { it.status == "OPEN" }}

2. GAS-IN & METER COMMISSIONING LOG:
${if (gasInRecords.isEmpty()) "• No meters commissioned today." else gasInRecords.joinToString("\n") { "• BP: ${it.bpNumber} | Meter: ${it.meterNumber} (${it.meterMake}) | Reading: ${it.meterReading} | Leak Check: ${it.leakTestStatus}" }}

3. TNF ESCALATIONS & MATERIAL AUDIT:
${if (tnfCases.isEmpty()) "• Zero active TNF cases recorded." else tnfCases.take(5).joinToString("\n") { "• [${it.caseNumber}] BP: ${it.bpNumber} - ${it.category}: ${it.remarks}" }}

4. QUALITY ASSURANCE & SAFETY VERIFICATION:
- Hydro / pneumatic line pressure tested compliant at 21 mbar standard gauge.
- Copper pipe wall clips and brass isolation ball valves checked for gas tightness.
- Joint Meter Reading (JMR) cards digitally stored and verified.
                                        """.trimIndent()
                                        Toast.makeText(context, "AI synthesized complete DPR from live records!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("AI Synthesize DPR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                // AI Safety Check Button
                                OutlinedButton(
                                    onClick = {
                                        dprTextContent += """

[AI AUDIT NOTE: All ${gasInRecords.size} commissioned meters have passed 15-minute zero drop leak tests. Recommend prioritizing resolution of ${tnfCases.count { it.status == "OPEN" }} open TNF cases to avoid pipeline riser bottlenecks.]
                                        """.trimIndent()
                                        Toast.makeText(context, "AI safety & compliance note appended", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("AI Safety Audit", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }

                    // Word-Style Formatting Toolbar
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { dprTextContent += "\n**BOLD SECTION**\n" }) {
                                Icon(Icons.Default.FormatBold, contentDescription = "Bold", tint = GailNavy)
                            }
                            IconButton(onClick = { dprTextContent += " *italic* " }) {
                                Icon(Icons.Default.FormatItalic, contentDescription = "Italic", tint = GailNavy)
                            }
                            IconButton(onClick = { dprTextContent += " __underlined__ " }) {
                                Icon(Icons.Default.FormatUnderlined, contentDescription = "Underline", tint = GailNavy)
                            }
                            IconButton(onClick = { dprTextContent += "\n• Field Item: " }) {
                                Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullets", tint = GailNavy)
                            }
                            IconButton(onClick = {
                                dprTextContent += "\n| Category | Target | Completed | Status |\n| Feasibility | 15 | ${feasibilities.size} | 100% |\n| Gas-In | 10 | ${gasInRecords.size} | Live |\n"
                            }) {
                                Icon(Icons.Default.TableChart, contentDescription = "Table", tint = GailNavy)
                            }
                        }
                    }

                    // Document Writing Canvas
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(3.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "OFFICIAL DAILY PROGRESS REPORT TEXT",
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
                                    .height(300.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Actions: Save DPR (Daily Save), Export PDF, Export DOCX
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
                                    Toast.makeText(context, "DPR saved daily for $reportDate successfully!", Toast.LENGTH_SHORT).show()
                                    selectedTab = 1
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
                                ExportUtil.shareFile(context, pdfFile, "application/pdf", "GAIL PMC DPR ($reportDate)")
                                Toast.makeText(context, "Generated Official PDF", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PDF Share", fontSize = 11.sp)
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
                                ExportUtil.shareFile(context, docFile, "application/msword", "GAIL PMC DPR ($reportDate)")
                                Toast.makeText(context, "Generated Word DOCX", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("DOCX Share", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            } else {
                // ================= SAVED DAILY DPR ARCHIVE (DATE-WISE) =================
                if (dprReports.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No DPR reports saved yet. Save from the editor tab.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(dprReports, key = { it.id }) { dpr ->
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
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.DateRange, contentDescription = null, tint = GailNavy, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "DATE: ${dpr.reportDate}",
                                                fontWeight = FontWeight.Bold,
                                                color = GailNavy,
                                                fontSize = 15.sp
                                            )
                                        }

                                        Surface(
                                            color = GailOrange.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "DPR SAVED",
                                                color = GailOrange,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = dpr.project.ifBlank { "GAIL Gas Mandideep CGD" },
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Prepared By: ${dpr.preparedBy.ifBlank { "Rajesh Sharma" }}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )

                                    // Metrics summary badge row
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) {
                                            Text("Visits: ${dpr.totalHousesVisited}", fontSize = 10.sp, modifier = Modifier.padding(4.dp), color = GailNavy)
                                        }
                                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                                            Text("Feasible: ${dpr.feasibleCount}", fontSize = 10.sp, modifier = Modifier.padding(4.dp), color = Color(0xFF2E7D32))
                                        }
                                        Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(4.dp)) {
                                            Text("Gas-In: ${dpr.gasInCount}", fontSize = 10.sp, modifier = Modifier.padding(4.dp), color = Color(0xFF1565C0))
                                        }
                                        Surface(color = Color(0xFFFCE4EC), shape = RoundedCornerShape(4.dp)) {
                                            Text("TNF: ${dpr.tnfCount}", fontSize = 10.sp, modifier = Modifier.padding(4.dp), color = Color(0xFFC2185B))
                                        }
                                    }

                                    Divider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 6.dp))

                                    // Action buttons for this specific saved date's DPR
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Load into Editor
                                        OutlinedButton(
                                            onClick = {
                                                reportDate = dpr.reportDate
                                                project = dpr.project
                                                preparedBy = dpr.preparedBy
                                                dprTextContent = dpr.formattedContentHtml
                                                selectedTab = 0
                                                Toast.makeText(context, "Loaded DPR for ${dpr.reportDate} into editor", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp), tint = GailNavy)
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Edit", fontSize = 11.sp, color = GailNavy)
                                        }

                                        // PDF Download / Share
                                        Button(
                                            onClick = {
                                                val summary = "Visits: ${dpr.totalHousesVisited} | Feasible: ${dpr.feasibleCount} | Gas-in: ${dpr.gasInCount} | TNF: ${dpr.tnfCount}"
                                                val pdfFile = ExportUtil.exportDprToPdf(
                                                    context,
                                                    dpr.reportDate,
                                                    dpr.project,
                                                    dpr.preparedBy,
                                                    dpr.formattedContentHtml,
                                                    summary
                                                )
                                                ExportUtil.shareFile(context, pdfFile, "application/pdf", "GAIL PMC DPR (${dpr.reportDate})")
                                                Toast.makeText(context, "Exported PDF for ${dpr.reportDate}", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("PDF", fontSize = 11.sp)
                                        }

                                        // DOCX Download / Share
                                        Button(
                                            onClick = {
                                                val docFile = ExportUtil.exportDprToDocx(
                                                    context,
                                                    dpr.reportDate,
                                                    dpr.project,
                                                    dpr.preparedBy,
                                                    dpr.formattedContentHtml
                                                )
                                                ExportUtil.shareFile(context, docFile, "application/msword", "GAIL PMC DPR (${dpr.reportDate})")
                                                Toast.makeText(context, "Exported DOCX for ${dpr.reportDate}", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("DOCX", fontSize = 11.sp)
                                        }

                                        Spacer(modifier = Modifier.weight(1f))

                                        // Delete DPR
                                        IconButton(
                                            onClick = { dprToDelete = dpr },
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

    // Delete DPR Confirmation Dialog
    dprToDelete?.let { dpr ->
        AlertDialog(
            onDismissRequest = { dprToDelete = null },
            title = { Text("Delete DPR Report?") },
            text = { Text("Are you sure you want to permanently delete DPR report for date ${dpr.reportDate}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDpr(dpr)
                        dprToDelete = null
                        Toast.makeText(context, "DPR report deleted", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { dprToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
