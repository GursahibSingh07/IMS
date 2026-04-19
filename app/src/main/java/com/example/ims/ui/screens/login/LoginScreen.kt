package com.example.ims.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ims.core.MockAuthService
import com.example.ims.core.MockUserProfile

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: (MockUserProfile) -> Unit
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val (tempUsername, tempPassword) = remember { MockAuthService.temporaryCredentials() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        // Decorative circles from IMS screen.svg
        Box(
            modifier = Modifier
                .requiredWidth(400.dp)
                .requiredHeight(400.dp)
                .offset(x = 41.dp, y = (-88).dp)
                .clip(RoundedCornerShape(200.dp))
                .background(Color(0x0D00113A))
        )
        Box(
            modifier = Modifier
                .requiredWidth(300.dp)
                .requiredHeight(300.dp)
                .offset(x = (-19).dp, y = 628.dp)
                .clip(RoundedCornerShape(150.dp))
                .background(Color(0x33D1E1F4))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 188.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoBlock()
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "IMS Portal",
                style = TextStyle(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF00113A)
                )
            )
            Text(
                text = "A simple institute workspace for daily operations.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF50606F),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier
                    .requiredWidth(354.dp)
                    .requiredHeight(313.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "EMAIL OR USERNAME",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF50606F)
                    )

                    LoginField(
                        value = username,
                        onValueChange = { username = it },
                        hint = "Enter username",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AlternateEmail,
                                contentDescription = null,
                                tint = Color(0xFF475569)
                            )
                        },
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )

                    Text(
                        text = "PASSWORD",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF50606F)
                    )

                    LoginField(
                        value = password,
                        onValueChange = { password = it },
                        hint = "............",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = Color(0xFF475569)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = if (showPassword) {
                                    Icons.Outlined.VisibilityOff
                                } else {
                                    Icons.Outlined.Visibility
                                },
                                contentDescription = "Toggle password visibility",
                                tint = Color(0xFF475569),
                                modifier = Modifier.clickable { showPassword = !showPassword }
                            )
                        },
                        visualTransformation = if (showPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )

                    LoginButton(
                        onClick = {
                            val profile = MockAuthService.validateCredentials(username, password)
                            if (profile != null) {
                                errorMessage = null
                                onLoginSuccess(profile)
                            } else {
                                errorMessage = "Invalid credentials. Use temporary credentials below."
                            }
                        }
                    )

                    Text(
                        text = "Temp: $tempUsername / $tempPassword",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF50606F)
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB42318)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LogoBlock() {
    Box(
        modifier = Modifier
            .requiredWidth(80.dp)
            .requiredHeight(67.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF00113A)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.School,
            contentDescription = "IMS logo",
            tint = Color.White
        )
    }
}

@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType,
    imeAction: ImeAction
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .requiredWidth(290.dp)
            .requiredHeight(51.dp),
        singleLine = true,
        textStyle = TextStyle(color = Color(0xFF00113A), fontSize = 15.sp),
        placeholder = {
            Text(
                text = hint,
                color = Color(0x99757682),
                fontSize = 14.sp
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF2F4F6),
            unfocusedContainerColor = Color(0xFFF2F4F6),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = Color(0xFF00113A),
            unfocusedTextColor = Color(0xFF00113A)
        )
    )
}

@Composable
private fun LoginButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .requiredWidth(290.dp)
            .requiredHeight(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF00113A), Color(0xFF0C1F56))
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Login to Dashboard",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}
