package com.example.gochatapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = AppBar,
    onPrimary = White,
    background = White,
    onBackground = Black,
)

private val DarkColors = darkColorScheme(
    primary = AppBar,
    onPrimary = White,
    background = Black,
    onBackground = White,
)

@Composable
fun ChatTheme( darkTheme: Boolean = false,content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
