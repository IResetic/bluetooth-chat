package dev.skybit.bluetoothchat.core.presentation.style.tokens

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.skybit.bluetoothchat.R

object AppFont {
    val robotoFont = FontFamily(
        Font(R.font.roboto_regular, weight = FontWeight.Normal),
        Font(R.font.roboto_medium, weight = FontWeight.Medium),
        Font(R.font.roboto_bold, weight = FontWeight.SemiBold),
        Font(R.font.roboto_bold, weight = FontWeight.Bold)
    )
}
