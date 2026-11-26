package com.example.studyguide.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4638C9),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAE7FF),
    onPrimaryContainer = Color(0xFF4032B8),
    background = Color(0xFFF5F6FC),
    onBackground = Color(0xFF191B36),
    surface = Color.White,
    onSurface = Color(0xFF191B36),
    onSurfaceVariant = Color(0xFF505875),
    surfaceContainerHigh = Color(0xFFF0F0FC),
    outline = Color(0xFF68708D),
    outlineVariant = Color(0xFFDADDF0)
)

internal val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFC4BFFF),
    onPrimary = Color(0xFF241779),
    primaryContainer = Color(0xFF342A68),
    onPrimaryContainer = Color(0xFFD6D0FF),
    background = Color(0xFF111220),
    onBackground = Color(0xFFF1EFFC),
    surface = Color(0xFF1B1D30),
    onSurface = Color(0xFFF1EFFC),
    onSurfaceVariant = Color(0xFFBCC2DB),
    surfaceContainerHigh = Color(0xFF252741),
    outline = Color(0xFF979DBB),
    outlineVariant = Color(0xFF3C405D)
)
