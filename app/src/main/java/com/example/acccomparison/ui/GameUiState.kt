package com.example.acccomparison.ui

import androidx.compose.ui.graphics.Color

data class GameUiState(
    var accStatus: Boolean = false,
    var excellents: Int = 0,
    var goods: Int  = 0,
    var oks: Int = 0,
    var totals: Int = 0,
    var color: Color = Color(0xFF6200EE)
)
