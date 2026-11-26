package com.example.studyguide.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

private val defaults = Typography()

internal val Typography = Typography(
    headlineMedium = defaults.headlineMedium.copy(fontWeight = FontWeight.Bold),
    titleLarge = defaults.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = defaults.titleMedium.copy(fontWeight = FontWeight.SemiBold)
)
