package com.huhx0015.coffeenearby.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = CoffeeBrown,
    onPrimary = Color.White,
    primaryContainer = CoffeeBrownLight,
    onPrimaryContainer = Color.White,
    secondary = CoffeeAccent,
    onSecondary = CoffeeBrown,
    background = CoffeeBackground,
    onBackground = Color.Black,
    surface = CoffeeSurface,
    onSurface = Color.Black,
    surfaceVariant = CoffeeAccent,
    onSurfaceVariant = CoffeeBrown
)

private val DarkColorScheme = darkColorScheme(
    primary = CoffeeBrownDarkTheme,
    onPrimary = Color.White,
    primaryContainer = CoffeeBrownLightTheme,
    onPrimaryContainer = Color.White,
    secondary = CoffeeAccentDark,
    onSecondary = CoffeeBrown,
    background = CoffeeBackgroundDark,
    onBackground = Color.Black,
    surface = CoffeeSurfaceDark,
    onSurface = Color.Black,
    surfaceVariant = CoffeeAccent,
    onSurfaceVariant = CoffeeBrown
)

@Composable
fun CoffeeNearbyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) {
                CoffeeBrownDarker.toArgb()
            } else {
                CoffeeBrownDark.toArgb()
            }
            window.navigationBarColor = if (darkTheme) {
                CoffeeBackgroundDark.toArgb()
            } else {
                Color.White.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
