package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EnchantmentPurpleNeon,
    onPrimary = ObsidianBlack,
    secondary = DiamondCyan,
    onSecondary = ObsidianBlack,
    tertiary = ExperienceGreen,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = OffWhiteText,
    surface = DarkCardBg,
    onSurface = OffWhiteText,
    outline = LightGrayBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Mode as requested for the custom Minecraft premium visual gamer style
    dynamicColor: Boolean = false, // Set to false to prioritize our handcrafted Minecraft UI tokens
    content: @Composable () -> Unit,
) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
