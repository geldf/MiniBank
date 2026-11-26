package com.angelhia.minibank.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/** Result of a biometric authentication attempt. */
sealed interface BiometricResult {
    data object Success : BiometricResult
    /** User tapped the negative button ("Use password") or dismissed the prompt. */
    data object Cancelled : BiometricResult
    data class Error(val code: Int, val message: String) : BiometricResult
}

/** Whether biometrics can be used on this device right now. */
enum class BiometricAvailability {
    Available,
    NoHardware,
    HardwareUnavailable,
    NotEnrolled,
    SecurityUpdateRequired,
    Unsupported,
}

/**
 * Thin wrapper around [BiometricPrompt] / [BiometricManager].
 *
 * Uses [BIOMETRIC_STRONG] (Class 3) which is what you want for a banking app: it is the
 * only class that can later be bound to a Keystore key via [BiometricPrompt.CryptoObject].
 */
class BiometricAuthenticator(private val context: Context) {

    private val biometricManager = BiometricManager.from(context)

    fun availability(): BiometricAvailability =
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NotEnrolled
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricAvailability.SecurityUpdateRequired
            else -> BiometricAvailability.Unsupported
        }

    fun authenticate(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        negativeButtonText: String,
        onResult: (BiometricResult) -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onResult(BiometricResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                val cancelled = errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_CANCELED
                onResult(
                    if (cancelled) BiometricResult.Cancelled
                    else BiometricResult.Error(errorCode, errString.toString())
                )
            }

            // onAuthenticationFailed() = a single non-matching finger/face. The system prompt
            // already shows feedback and lets the user retry, so nothing to do here.
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setConfirmationRequired(false)
            .build()

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }
}
