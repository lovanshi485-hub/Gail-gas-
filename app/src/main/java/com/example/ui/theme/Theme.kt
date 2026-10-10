package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val GailDarkColorScheme = darkColorScheme(
  primary = GailOrangeLight,
  onPrimary = GailNavy,
  primaryContainer = GailNavyLight,
  onPrimaryContainer = Color.White,
  secondary = GailGold,
  onSecondary = Color.Black,
  tertiary = GailAmber,
  background = SurfaceDark,
  surface = SurfaceCardDark,
  onBackground = TextPrimaryLight,
  onSurface = TextPrimaryLight
)

private val GailLightColorScheme = lightColorScheme(
  primary = GailNavy,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE2EAF4),
  onPrimaryContainer = GailNavy,
  secondary = GailOrange,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFFFECE0),
  onSecondaryContainer = GailOrange,
  tertiary = GailGold,
  background = SurfaceLight,
  surface = SurfaceCardLight,
  onBackground = TextPrimaryDark,
  onSurface = TextPrimaryDark
)

@Composable
fun GailGasTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use corporate branding by default for industrial fidelity
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> GailDarkColorScheme
    else -> GailLightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
