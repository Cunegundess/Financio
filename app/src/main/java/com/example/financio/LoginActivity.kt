package com.example.financio

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financio.ui.theme.BiometricAuthTheme
import com.example.financio.ui.theme.PoppinsFont

class LoginActivity : AppCompatActivity() {

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiometricAuthTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val biometricResult by promptManager.promptResults.collectAsState(initial = null)
                    val enrollLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = {
                            println("Activity result: $it")
                        }
                    )

                    LaunchedEffect(biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
                        if (Build.VERSION.SDK_INT >= 30) {
                            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                                )
                            }
                            enrollLauncher.launch(enrollIntent)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(80.dp))

                        val title = AnnotatedString.Builder("Financio.")
                            .apply {
                                addStyle(
                                    style = SpanStyle(
                                        color = colorResource(id = R.color.green),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 50.sp
                                    ),
                                    start = 6,
                                    end = 9
                                )
                            }
                            .toAnnotatedString()

                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            fontFamily = PoppinsFont,
                            fontWeight = FontWeight.Black,
                            fontSize = 50.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Your Personal Finance Buddy",
                            style = MaterialTheme.typography.titleSmall,
                            textAlign = TextAlign.Center,
                            fontFamily = PoppinsFont,
                            fontWeight = FontWeight.Thin,
                            fontStyle = FontStyle.Italic,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                onClick = {
                                    promptManager.showBiometricPrompt(
                                        title = "Biometric Prompt",
                                        description = "Sample prompt description"
                                    )
                                },
                                modifier = Modifier
                                    .padding(50.dp)
                                    .width(200.dp)
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.green),
                                )
                            ) {
                                Text(
                                    text = "Enter",
                                    fontFamily = PoppinsFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            biometricResult?.let { result ->
                                Text(
                                    text = when (result) {
                                        is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                                            result.error
                                        }

                                        BiometricPromptManager.BiometricResult.AuthenticationFailed -> "Biometric authentication failed"
                                        BiometricPromptManager.BiometricResult.AuthenticationNotSet -> "Biometric authentication not set"
                                        BiometricPromptManager.BiometricResult.AuthenticationSucceeded -> "Biometric authentication successful"
                                        BiometricPromptManager.BiometricResult.FeatureUnavailable -> "Biometric authentication feature is not available"
                                        BiometricPromptManager.BiometricResult.HardwareUnavailable -> "Biometric authentication hardware is not available"
                                    },
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Thin,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}