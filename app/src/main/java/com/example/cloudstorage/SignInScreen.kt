package com.example.cloudstorage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudstorage.ui.theme.CloudBlue
import com.example.cloudstorage.ui.theme.CloudBlueSplash
import com.example.cloudstorage.ui.theme.CloudSky
import com.example.cloudstorage.ui.theme.DividerColor
import com.example.cloudstorage.ui.theme.ErrorRed
import com.example.cloudstorage.ui.theme.NeutralDark
import com.example.cloudstorage.ui.theme.NeutralLight
import com.example.cloudstorage.ui.theme.NeutralMedium
import com.example.cloudstorage.ui.theme.NeutralWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        var valid = true
        emailError = if (email.isBlank()) {
            valid = false; "Email is required"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false; "Enter a valid email address"
        } else null

        passwordError = if (password.isBlank()) {
            valid = false; "Password is required"
        } else if (password.length < 6) {
            valid = false; "Password must be at least 6 characters"
        } else null

        return valid
    }

    fun onSignIn() {
        if (!validate()) return
        scope.launch {
            isLoading = true
            delay(1200) // simulate network call
            isLoading = false
            onSignInSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeutralWhite)
    ) {
        // Top accent header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(CloudBlueSplash, CloudBlue)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_cloudbox_logo),
                    contentDescription = "CloudBox",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "CloudBox",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = stringResource(R.string.sign_in),
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }

        // White card form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 180.dp)
                .background(
                    color = NeutralWhite,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // ── Email field ────────────────────────────────────────────────
            FieldLabel(text = stringResource(R.string.email))
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                placeholder = { Text("you@example.com", color = NeutralMedium, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = if (emailError != null) ErrorRed else NeutralMedium
                    )
                },
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors(),
                textStyle = TextStyle(fontSize = 15.sp, color = NeutralDark),
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = emailError != null) {
                Text(
                    text = emailError ?: "",
                    color = ErrorRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ── Password field ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FieldLabel(text = stringResource(R.string.password))
                TextButton(
                    onClick = { /* TODO: forgot password flow */ },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.forgot_password),
                        color = CloudBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                placeholder = { Text("Enter your password", color = NeutralMedium, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (passwordError != null) ErrorRed else NeutralMedium
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                                          else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = NeutralMedium
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                isError = passwordError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    onSignIn()
                }),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = textFieldColors(),
                textStyle = TextStyle(fontSize = 15.sp, color = NeutralDark),
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(visible = passwordError != null) {
                Text(
                    text = passwordError ?: "",
                    color = ErrorRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Sign In button ─────────────────────────────────────────────
            Button(
                onClick = { onSignIn() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CloudBlueSplash,
                    disabledContainerColor = CloudBlueSplash.copy(alpha = 0.6f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.sign_in),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Divider ────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
                Text(
                    text = "  " + stringResource(R.string.or_continue_with_email) + "  ",
                    fontSize = 13.sp,
                    color = NeutralMedium
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Google button ──────────────────────────────────────────────
            OutlinedButton(
                onClick = onSignInSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, DividerColor),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = NeutralWhite)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.continue_with_google),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeutralDark
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Sign Up prompt ─────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val dontHaveAcc = stringResource(R.string.dont_have_account)
                val createAcc = stringResource(R.string.create_account)
                Text(
                    text = buildAnnotatedString {
                        append("$dontHaveAcc ")
                        withStyle(SpanStyle(color = CloudBlue, fontWeight = FontWeight.SemiBold)) {
                            append(createAcc)
                        }
                    },
                    fontSize = 14.sp,
                    color = NeutralMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = NeutralDark
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CloudBlue,
    unfocusedBorderColor = DividerColor,
    errorBorderColor = ErrorRed,
    focusedContainerColor = NeutralLight,
    unfocusedContainerColor = NeutralLight,
    errorContainerColor = ErrorRed.copy(alpha = 0.05f),
    cursorColor = CloudBlue
)
