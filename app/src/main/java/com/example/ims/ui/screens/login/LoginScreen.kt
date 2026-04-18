package com.example.ims.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
    ) {
        DecorativeBlobs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoBlock()
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "IMS Portal",
                style = TextStyle(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF00113A)
                )
            )
            Text(
                text = "Education panel sign in",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF50606F),
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Email address",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF50606F)
                    )
                    LoginField(
                        value = email,
                        onValueChange = { email = it },
                        hint = "username@institute.edu",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AlternateEmail,
                                contentDescription = null,
                                tint = Color(0xFF757682)
                            )
                        },
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )

                    Text(
                        text = "Password",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF50606F)
                    )
                    LoginField(
                        value = password,
                        onValueChange = { password = it },
                        hint = "................",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = Color(0xFF757682)
                            )
                        },
                        trailingIcon = {
                            val icon = if (showPassword) {
                                Icons.Outlined.VisibilityOff
                            } else {
                                Icons.Outlined.Visibility
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = "Toggle password visibility",
                                tint = Color(0xFF757682),
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remember me",
                            color = Color(0xFF50606F),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Forgot password?",
                            color = Color(0xFF00113A),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    LoginButton(onClick = onLoginClick)
                }
            }
        }
    }
}

@Composable
private fun BoxScope.DecorativeBlobs() {
    Box(
        modifier = Modifier
            .size(300.dp)
            .align(Alignment.TopEnd)
            .padding(top = 8.dp, end = 6.dp)
            .clip(CircleShape)
            .background(Color(0x0D00113A))
    )

    Box(
        modifier = Modifier
            .size(220.dp)
            .align(Alignment.BottomStart)
            .padding(start = 8.dp, bottom = 20.dp)
            .clip(CircleShape)
            .background(Color(0x33D1E1F4))
    )
}

@Composable
private fun LogoBlock() {
    Box(
        modifier = Modifier
            .size(width = 80.dp, height = 67.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF00113A)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.School,
            contentDescription = "IMS logo",
            tint = Color.White,
            modifier = Modifier.size(34.dp)
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
            .fillMaxWidth()
            .height(56.dp),
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
            disabledBorderColor = Color.Transparent,
            focusedTextColor = Color(0xFF00113A),
            unfocusedTextColor = Color(0xFF00113A)
        )
    )
}

@Composable
private fun LoginButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF00113A), Color(0xFF0A1C52))
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Log in to continue",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = Color.White
        )
    }
}
