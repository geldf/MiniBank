package com.angelhia.minibank

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.angelhia.minibank.ui.login.LoginRoute
import com.angelhia.minibank.ui.theme.MiniBankTheme

// FragmentActivity (not ComponentActivity) because BiometricPrompt requires it.
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniBankTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var loggedIn by rememberSaveable { mutableStateOf(false) }
                    if (loggedIn) {
                        // Placeholder until a real home screen / navigation exists.
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("You're logged in 🎉", style = MaterialTheme.typography.headlineSmall)
                        }
                    } else {
                        LoginRoute(onLoginSuccess = { loggedIn = true })
                    }
                }
            }
        }
    }
}
