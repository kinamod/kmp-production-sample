package com.github.jetbrains.rssreader.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Orange = Color(0xfff8873c)
private val Purple = Color(0xff6b70fc)
private val DarkColors = darkColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    secondary = Purple,
    onSecondary = Color.White
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = {
            Surface(content = content)
        }
    )
}
