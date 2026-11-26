package com.tecnoayuda.extractorfacturas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandNeonMint,
    onPrimary = BrandNavyBackground,
    primaryContainer = BrandRichBlue,
    onPrimaryContainer = Color.White,
    secondary = BrandAquaBlue,
    onSecondary = BrandNavyBackground,
    secondaryContainer = BrandDarkCard,
    onSecondaryContainer = BrandNeonMint,
    background = BrandNavyBackground,
    surface = BrandNavyBackground,
    onBackground = Color.White,
    onSurface = BrandIceWhite
)

private val LightColorScheme = lightColorScheme(
    primary = BrandRichBlue,
    onPrimary = Color.White,
    primaryContainer = BrandIceWhite,
    onPrimaryContainer = BrandRichBlue,
    secondary = BrandAquaBlue,
    onSecondary = Color.White,
    secondaryContainer = BrandNeonMint,
    onSecondaryContainer = BrandNavyBackground,
    background = Color(0xFFF5F9FF),
    surface = Color.White,
    onBackground = BrandNavyBackground,
    onSurface = BrandNavyBackground
)

@Composable
fun ExtractorDeFacturasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // FORCE FALSE to use our brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}