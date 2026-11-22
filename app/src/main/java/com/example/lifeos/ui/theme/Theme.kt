
package com.example.lifeos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LifeOSPurpleSoft,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = LifeOSPurple,
    onPrimary = LifeOSSurface,

    primaryContainer = LifeOSPurpleSoft,
    onPrimaryContainer = LifeOSPurpleDark,

    secondary = LifeOSPurpleDark,
    onSecondary = LifeOSSurface,

    tertiary = LifeOSGreen,
    onTertiary = LifeOSSurface,

    background = LifeOSBackground,
    onBackground = LifeOSText,

    surface = LifeOSSurface,
    onSurface = LifeOSText,

    surfaceVariant = LifeOSSurfaceVariant,
    onSurfaceVariant = LifeOSMuted,

    outline = LifeOSOutline,

    error = LifeOSRed,
    onError = LifeOSSurface
)

@Composable
fun LIFEOSTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && darkTheme -> DarkColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
