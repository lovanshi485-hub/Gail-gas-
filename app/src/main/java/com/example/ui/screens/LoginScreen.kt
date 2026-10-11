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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: GailGasViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    // Primary Tab: 0 = Sign In, 1 = Register Account
    var primaryTabIndex by remember { mutableIntStateOf(0) }

    // Sign In Sub-type: 0 = OTP Login, 1 = Password Login
    var signinModeIndex by remember { mutableIntStateOf(0) }

    // Role: 0 = Admin, 1 = Field Engineer / Supervisor (Guest removed per user requirement)
    var roleIndex by remember { mutableIntStateOf(1) }

    // Login Fields
    var loginMobile by remember { mutableStateOf("9826012345") }
    var loginIdentifier by remember { mutableStateOf("rajesh.pmc@gailgas.co.in") }
    var loginPassword by remember { mutableStateOf("Gail@2026") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // OTP states
    var enteredOtp by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf<String?>(null) }
    var otpCooldown by remember { mutableIntStateOf(0) }

    // Registration Fields
    var regFullName by remember { mutableStateOf("") }
    var regEmployeeId by remember { mutableStateOf("GG-PMC-${(1000..9999).random()}") }
    var regDesignation by remember { mutableStateOf("PMC Field Supervisor") }
    var regMobile by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regCity by remember { mutableStateOf("Mandideep") }
    var regProject by remember { mutableStateOf("GAIL Gas Mandideep CGD") }
    var regPassword by remember { mutableStateOf("") }
    var regEnteredOtp by remember { mutableStateOf("") }
    var regGeneratedOtp by remember { mutableStateOf<String?>(null) }
    var regOtpCooldown by remember { mutableIntStateOf(0) }

    // Timer countdown for Login OTP
    LaunchedEffect(otpCooldown) {
        if (otpCooldown > 0) {
            delay(1000L)
            otpCooldown -= 1
        }
    }

    // Timer countdown for Reg OTP
    LaunchedEffect(regOtpCooldown) {
        if (regOtpCooldown > 0) {
            delay(1000L)
            regOtpCooldown -= 1
        }
    }

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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // App Emblem / Logo
            Surface(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape),
                color = Color.White.copy(alpha = 0.12f),
                tonalElevation = 6.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_gail_gas_logo_1791593090147),
                    contentDescription = "GAIL Gas Logo",
                    modifier = Modifier.padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "GAIL GAS LIMITED",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Text(
                text = "PMC FIELD MANAGER PRO",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GailOrangeLight,
                letterSpacing = 2.sp
            )

            Text(
                text = "City Gas Distribution Operations & Asset Management",
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 18.dp)
            )

            // Main Interactive Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {

                    // Tab: Sign In vs Register
                    TabRow(
                        selectedTabIndex = primaryTabIndex,
                        containerColor = Color.Transparent,
                        contentColor = GailOrange,
                        divider = {}
                    ) {
                        Tab(
                            selected = primaryTabIndex == 0,
                            onClick = { primaryTabIndex = 0 },
                            text = { Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        )
                        Tab(
                            selected = primaryTabIndex == 1,
                            onClick = { primaryTabIndex = 1 },
                            text = { Text("New Registration", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (primaryTabIndex == 0) {
                        // ================= LOGIN FLOW =================
                        // Role chips (Admin vs Employee/Supervisor)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = roleIndex == 1,
                                onClick = { roleIndex = 1 },
                                label = { Text("Field Officer / Supervisor", fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.Engineering, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = roleIndex == 0,
                                onClick = { roleIndex = 0 },
                                label = { Text("PMC Admin", fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Switch between OTP Login and Password Login
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { signinModeIndex = 0 },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (signinModeIndex == 0) GailNavy.copy(alpha = 0.08f) else Color.Transparent
                                )
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(14.dp), tint = GailNavy)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("OTP Login", fontSize = 11.sp, fontWeight = if (signinModeIndex == 0) FontWeight.Bold else FontWeight.Normal)
                            }

                            OutlinedButton(
                                onClick = { signinModeIndex = 1 },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (signinModeIndex == 1) GailNavy.copy(alpha = 0.08f) else Color.Transparent
                                )
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp), tint = GailNavy)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Password", fontSize = 11.sp, fontWeight = if (signinModeIndex == 1) FontWeight.Bold else FontWeight.Normal)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (signinModeIndex == 0) {
                            // OTP LOGIN
                            OutlinedTextField(
                                value = loginMobile,
                                onValueChange = { loginMobile = it },
                                label = { Text("Registered Mobile Number") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GailNavy) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = enteredOtp,
                                    onValueChange = { enteredOtp = it },
                                    label = { Text("Enter OTP") },
                                    placeholder = { Text("e.g. 4826") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Button(
                                    onClick = {
                                        if (loginMobile.length < 10) {
                                            Toast.makeText(context, "Valid 10-digit mobile number likhein", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        val generated = (1000..9999).random().toString()
                                        generatedOtp = generated
                                        enteredOtp = generated
                                        otpCooldown = 45
                                        Toast.makeText(context, "GAIL Gas OTP Sent: $generated", Toast.LENGTH_LONG).show()
                                    },
                                    enabled = otpCooldown == 0,
                                    colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(52.dp)
                                ) {
                                    Text(
                                        text = if (otpCooldown > 0) "${otpCooldown}s" else "Send OTP",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            generatedOtp?.let { code ->
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                ) {
                                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("OTP received for $loginMobile: $code", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    if (enteredOtp.isBlank() || (generatedOtp != null && enteredOtp != generatedOtp)) {
                                        Toast.makeText(context, "Sahi OTP darj karein", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    val role = if (roleIndex == 0) UserRole.ADMIN else UserRole.SUPERVISOR
                                    viewModel.loginWithRole(role, loginMobile)
                                    Toast.makeText(context, "Login Safal Hua!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GailNavy),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Verify OTP & Sign In", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // PASSWORD LOGIN
                            OutlinedTextField(
                                value = if (roleIndex == 0) "admin.pmc" else loginIdentifier,
                                onValueChange = { loginIdentifier = it },
                                label = { Text(if (roleIndex == 0) "Admin Username" else "Email / Mobile / Employee ID") },
                                leadingIcon = { Icon(if (roleIndex == 0) Icons.Default.AdminPanelSettings else Icons.Default.Person, contentDescription = null, tint = GailNavy) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                label = { Text("Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GailNavy) },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    val role = if (roleIndex == 0) UserRole.ADMIN else UserRole.SUPERVISOR
                                    viewModel.loginWithRole(role, loginIdentifier)
                                    Toast.makeText(context, "Welcome back to GAIL Gas Field Manager Pro", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GailNavy),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Sign In Securely", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // ================= REGISTRATION FLOW =================
                        Text(
                            text = "REGISTER NEW PMC FIELD OFFICER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GailNavy,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regFullName,
                            onValueChange = { regFullName = it },
                            label = { Text("Full Name *") },
                            placeholder = { Text("e.g. Ramesh Patel") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GailNavy) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = regEmployeeId,
                                onValueChange = { regEmployeeId = it },
                                label = { Text("Employee ID") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = GailNavy) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = regDesignation,
                                onValueChange = { regDesignation = it },
                                label = { Text("Designation") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regMobile,
                            onValueChange = { regMobile = it },
                            label = { Text("Mobile Number *") },
                            placeholder = { Text("10-digit Mobile Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GailNavy) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Official Email (Optional)") },
                            placeholder = { Text("officer@gailgas.co.in") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GailNavy) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = regCity,
                                onValueChange = { regCity = it },
                                label = { Text("City") },
                                leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = GailNavy) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = regProject,
                                onValueChange = { regProject = it },
                                label = { Text("Assigned Project") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Set Password *") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GailNavy) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // OTP Verification for Registration
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = regEnteredOtp,
                                onValueChange = { regEnteredOtp = it },
                                label = { Text("Mobile OTP") },
                                placeholder = { Text("OTP code") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Button(
                                onClick = {
                                    if (regMobile.length < 10) {
                                        Toast.makeText(context, "10-digit mobile number likhein", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    val code = (1000..9999).random().toString()
                                    regGeneratedOtp = code
                                    regEnteredOtp = code
                                    regOtpCooldown = 45
                                    Toast.makeText(context, "GAIL Gas Registration OTP: $code", Toast.LENGTH_LONG).show()
                                },
                                enabled = regOtpCooldown == 0,
                                colors = ButtonDefaults.buttonColors(containerColor = GailOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Text(
                                    text = if (regOtpCooldown > 0) "${regOtpCooldown}s" else "Send OTP",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        regGeneratedOtp?.let { code ->
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Verification Code: $code", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (regFullName.isBlank() || regMobile.isBlank()) {
                                    Toast.makeText(context, "Name aur Mobile Number likhein", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (regEnteredOtp.isBlank() || (regGeneratedOtp != null && regEnteredOtp != regGeneratedOtp)) {
                                    Toast.makeText(context, "OTP verify karein", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.registerNewOfficer(
                                    name = regFullName.trim(),
                                    mobile = regMobile.trim(),
                                    email = if (regEmail.isBlank()) "${regMobile.trim()}@gailgas.co.in" else regEmail.trim(),
                                    designation = regDesignation.trim(),
                                    project = regProject.trim()
                                )
                                Toast.makeText(context, "Registration Kamyab! Welcome, ${regFullName.trim()}", Toast.LENGTH_LONG).show()
                                onLoginSuccess()
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF198754)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Complete Registration & Enter App", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Official PMC Field Execution Platform • GAIL Gas Limited",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
