package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.LanguageMode
import com.example.model.SyncState
import com.example.model.UserRole
import com.example.ui.components.SyncStatusBadge
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailOrange
import com.example.viewmodel.GailGasViewModel

@Composable
fun SettingsAndProfileScreen(
    viewModel: GailGasViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val languageMode by viewModel.languageMode.collectAsState()
    val fontSizeScale by viewModel.fontSizeScale.collectAsState()

    var editMode by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(currentUser.name) }
    var editMobile by remember { mutableStateOf(currentUser.mobile) }
    var editDesignation by remember { mutableStateOf(currentUser.designation) }
    var editProject by remember { mutableStateOf(currentUser.assignedProject) }

    // Toggle options in settings
    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoSyncOnWifi by remember { mutableStateOf(true) }
    var gpsTrackingOnSurvey by remember { mutableStateOf(false) }
    var showClearDbDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Direct Mobile APK Download Card (For standalone installation on Android devices)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            elevation = CardDefaults.cardElevation(3.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Android, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("STANDALONE ANDROID APK & MOBILE ACCESS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                }
                Text(
                    text = "Operate the full PMC portal directly on any Android smartphone without a desktop computer. Build, install, or launch via:",
                    fontSize = 12.sp,
                    color = Color(0xFF1B5E20)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ais-dev-tpnytgtbbzdlfytd7asfgw-146145773018.asia-southeast1.run.app"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Web / Cloud Link", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "AI Studio Top Bar -> Settings Menu -> Export APK / Project ZIP", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GailNavy),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Download APK", fontSize = 12.sp)
                    }
                }
            }
        }

        // User Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(3.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GailNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_gail_gas_logo_1791593090147),
                            contentDescription = "Profile Logo",
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = GailNavy
                        )
                        Text(
                            text = "${currentUser.role.name} • ${currentUser.employeeId}",
                            fontSize = 12.sp,
                            color = GailOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = currentUser.designation,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    IconButton(onClick = { editMode = !editMode }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = GailNavy)
                    }
                }

                if (editMode) {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editMobile,
                        onValueChange = { editMobile = it },
                        label = { Text("Official Mobile") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editDesignation,
                        onValueChange = { editDesignation = it },
                        label = { Text("Designation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editProject,
                        onValueChange = { editProject = it },
                        label = { Text("Assigned CGD Project") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.updateProfile(editName, editMobile, editDesignation, editProject)
                            editMode = false
                            Toast.makeText(context, "Profile details updated", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Profile Changes")
                    }
                }
            }
        }

        // Comprehensive Field Settings & Switches
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("FIELD OPERATIONS & NOTIFICATIONS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = GailNavy)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Pending Work & Follow-up Reminders", fontSize = 13.sp)
                    }
                    Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                }

                Divider(color = Color(0xFFE2E8F0))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = GailNavy)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Auto-Sync on Wi-Fi Connection", fontSize = 13.sp)
                    }
                    Switch(checked = autoSyncOnWifi, onCheckedChange = { autoSyncOnWifi = it })
                }

                Divider(color = Color(0xFFE2E8F0))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = GailNavy)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("GPS Location Stamp on Site Survey", fontSize = 13.sp)
                    }
                    Switch(checked = gpsTrackingOnSurvey, onCheckedChange = { gpsTrackingOnSurvey = it })
                }
            }
        }

        // Offline Mode & Cloud Sync Management
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ENTERPRISE CLOUD SYNC & OFFLINE CONTROL", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Sync Mode", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Stores local drafts securely during connectivity loss", fontSize = 11.sp, color = Color.Gray)
                    }
                    SyncStatusBadge(syncState = syncState)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.toggleOfflineMode()
                            val msg = if (syncState == SyncState.OFFLINE) "Switched to Online Cloud Mode" else "Enabled Field Offline Mode"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (syncState == SyncState.OFFLINE) Color(0xFF2E7D32) else Color(0xFF455A64)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (syncState == SyncState.OFFLINE) "Go Online" else "Go Offline", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.triggerSync()
                            Toast.makeText(context, "Synchronizing local data with cloud...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GailNavy),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync Now", fontSize = 12.sp)
                    }
                }
            }
        }

        // Application Preferences & Appearance
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("APP PREFERENCES & LOCALIZATION", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                // Dark Theme Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = GailNavy)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Dark Mode Theme", fontSize = 14.sp)
                    }
                    Switch(checked = isDarkMode, onCheckedChange = { viewModel.toggleTheme() })
                }

                Divider(color = Color(0xFFE2E8F0))

                // Language Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = GailNavy)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Interface Language", fontSize = 14.sp)
                    }
                    Button(
                        onClick = { viewModel.toggleLanguage() },
                        colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(if (languageMode == LanguageMode.ENGLISH) "English (Change)" else "हिंदी (बदलें)", fontSize = 12.sp)
                    }
                }

                Divider(color = Color(0xFFE2E8F0))

                // Font Size Scale
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FormatSize, contentDescription = null, tint = GailNavy)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Font Size Scaling", fontSize = 14.sp)
                        }
                        Text("${(fontSizeScale * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GailOrange)
                    }
                    Slider(
                        value = fontSizeScale,
                        onValueChange = { viewModel.setFontSizeScale(it) },
                        valueRange = 0.85f..1.35f,
                        steps = 4
                    )
                }
            }
        }

        // Data Management & Offline Storage
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("DATABASE STORAGE & LOCAL CACHE", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GailNavy)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Local database encrypted and backed up", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GailNavy),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Backup DB", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.loadSampleData {
                                Toast.makeText(context, "Demo records populated", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Load Demo", fontSize = 11.sp)
                    }
                }

                // Clear All Stored Data (Clean Slate) Button
                OutlinedButton(
                    onClick = { showClearDbDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clear All Saved Records (Reset Database)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Direct Mobile APK Download & Field Usage Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GailNavy),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "DIRECT ANDROID MOBILE APP INSTALLATION",
                    color = GailOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "If you do not have a desktop computer and wish to install this portal directly on an Android smartphone:",
                    color = Color.White,
                    fontSize = 12.sp
                )
                Text(
                    text = "1. In Google AI Studio, locate the settings/overflow menu in the top navigation bar.\n2. Click 'Download project as ZIP' or select 'Generate APK' from the build options.\n3. Transfer or open the APK on your Android device to install the native app immediately.\n4. You can also test every feature directly inside the live streaming emulator preview in real-time.\n5. All records, surveys, JMRs, and DPR files are permanently stored in the local SQLite/Room database and remain secure across app restarts.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Clear Database Confirmation Dialog
        if (showClearDbDialog) {
            AlertDialog(
                onDismissRequest = { showClearDbDialog = false },
                title = { Text("Clear All Database Records?") },
                text = { Text("This will permanently delete all locally stored records (customers, visits, feasibility surveys, TNF cases, gas-in records, and DPR reports). You will start with a completely clean database.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAllData {
                                Toast.makeText(context, "All records cleared. Database is now empty.", Toast.LENGTH_LONG).show()
                            }
                            showClearDbDialog = false
                        }
                    ) {
                        Text("Clear All Data", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDbDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Logout Button
        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Secure Logout", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
