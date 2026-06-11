package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GreenTealDarkPrimary,
    secondary = MintTealSecondary,
    tertiary = GoldYellowDark,
    background = DarkCharcoalBg,
    surface = CustomSurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = CustomOnSurfaceDark,
    onSurface = CustomOnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    secondary = EmeraldSecondary,
    tertiary = WarmGoldAccent,
    background = Color(0xFFF4F9F6),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF14201B),
    onSurface = Color(0xFF14201B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
