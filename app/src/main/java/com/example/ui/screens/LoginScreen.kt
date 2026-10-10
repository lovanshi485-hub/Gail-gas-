package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.UserRole
import com.example.ui.theme.GailNavy
import com.example.ui.theme.GailNavyLight
import com.example.ui.theme.GailOrange
import com.example.ui.theme.GailOrangeLight
import com.example.viewmodel.GailGasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: GailGasViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var selectedRoleIndex by remember { mutableIntStateOf(1) } // Default Employee

    // Login Method: 0 = Mobile Number, 1 = Email ID, 2 = Admin Username
    var loginMethodIndex by remember { mutableIntStateOf(0) }

    var loginIdentifier by remember { mutableStateOf("9826012345") }
    var password by remember { mutableStateOf("Gail@2026") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GailNavy, GailNavyLight, Color(0xFF0F172A))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // App Logo
            Surface(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                color = Color.White.copy(alpha = 0.1f),
                tonalElevation = 6.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_gail_gas_logo_1791593090147),
                    contentDescription = "GAIL Gas Logo",
                    modifier = Modifier.padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "GAIL GAS LIMITED",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Text(
                text = "PMC FIELD MANAGER PRO",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GailOrangeLight,
                letterSpacing = 2.sp
            )

            Text(
                text = "City Gas Distribution Operations & PMC Control",
                fontSize = 12.sp,
                color = Color(0xFFCBD5E1),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Role Selector Tabs (Admin, Employee, Guest)
                    TabRow(
                        selectedTabIndex = selectedRoleIndex,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedRoleIndex == 0,
                            onClick = {
                                selectedRoleIndex = 0
                                loginMethodIndex = 2 // Admin username
                                loginIdentifier = "admin.pmc"
                            },
                            text = { Text("Admin", fontSize = 13.sp) },
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = selectedRoleIndex == 1,
                            onClick = {
                                selectedRoleIndex = 1
                                loginMethodIndex = 0 // Mobile
                                loginIdentifier = "9826012345"
                            },
                            text = { Text("Employee", fontSize = 13.sp) },
                            icon = { Icon(Icons.Default.Engineering, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = selectedRoleIndex == 2,
                            onClick = {
                                selectedRoleIndex = 2
                                loginIdentifier = "GUEST-DEMO"
                            },
                            text = { Text("Guest", fontSize = 13.sp) },
                            icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedRoleIndex == 2) {
                        Surface(
                            color = Color(0xFFFFF8E1),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Restricted Guest / Sandbox Mode",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFB78103)
                                )
                                Text(
                                    text = "Bina password ke demo pipeline surveys, feasibility records aur DPR dekh sakte hain.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF5D4037),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        // Login Method Options: Mobile, Email, Admin Username
                        Text("LOGIN KAISE KAREIN:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GailNavy)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = loginMethodIndex == 0,
                                onClick = {
                                    loginMethodIndex = 0
                                    loginIdentifier = "9826012345"
                                },
                                label = { Text("Mobile No", fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            )
                            FilterChip(
                                selected = loginMethodIndex == 1,
                                onClick = {
                                    loginMethodIndex = 1
                                    loginIdentifier = "field.pmc@gailgas.co.in"
                                },
                                label = { Text("Email ID", fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            )
                            FilterChip(
                                selected = loginMethodIndex == 2,
                                onClick = {
                                    loginMethodIndex = 2
                                    loginIdentifier = "admin.pmc"
                                },
                                label = { Text("Admin ID", fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val (labelStr, leadingIconVec, keyboardTypeVal) = when (loginMethodIndex) {
                            0 -> Triple("Mobile Number *", Icons.Default.Phone, KeyboardType.Phone)
                            1 -> Triple("Email Address *", Icons.Default.Email, KeyboardType.Email)
                            else -> Triple("Admin Username *", Icons.Default.Person, KeyboardType.Text)
                        }

                        OutlinedTextField(
                            value = loginIdentifier,
                            onValueChange = { loginIdentifier = it },
                            label = { Text(labelStr) },
                            leadingIcon = { Icon(leadingIconVec, contentDescription = null, tint = GailNavy) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = keyboardTypeVal),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GailNavy) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            when (selectedRoleIndex) {
                                0 -> viewModel.login(UserRole.ADMIN, email = loginIdentifier)
                                1 -> viewModel.login(UserRole.EMPLOYEE, email = loginIdentifier, name = "Rajesh Sharma")
                                2 -> viewModel.login(UserRole.GUEST)
                            }
                            Toast.makeText(context, "Welcome to GAIL Gas PMC Pro", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedRoleIndex == 0) GailOrange else GailNavy
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = when (selectedRoleIndex) {
                                0 -> "Sign In as PMC Admin"
                                1 -> "Sign In as Field Employee"
                                else -> "Enter Restricted Guest Mode"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (selectedRoleIndex != 2) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "Password reset OTP sent to $loginIdentifier",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            ) {
                                Text("Forgot Password?", fontSize = 12.sp)
                            }

                            Text(
                                text = "v2.5 Production",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
