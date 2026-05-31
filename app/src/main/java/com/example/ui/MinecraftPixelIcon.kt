package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.DiamondCyan

@Composable
fun MinecraftPixelIcon(
    itemId: String,
    modifier: Modifier = Modifier,
    baseColor: Color = DiamondCyan
) {
    // Map item IDs to their corresponding drawable resource names
    val drawableResId = when (itemId) {
        "CAPACETE" -> R.drawable.capacete
        "PEITORAL" -> R.drawable.peitoral
        "CALÇAS" -> R.drawable.calcas
        "BOTAS" -> R.drawable.botas
        "ESPADA" -> R.drawable.espada
        "MACHADO" -> R.drawable.machado
        "PICARETA" -> R.drawable.picareta
        "PÁ" -> R.drawable.pa
        "ARCO" -> R.drawable.arco
        "MAÇA" -> R.drawable.maca
        "LANÇA" -> R.drawable.lanca
        "TRIDENTE" -> R.drawable.tridente
        "BESTA" -> R.drawable.besta
        "VARA" -> R.drawable.vara
        "ELYTRA" -> R.drawable.elytra
        "ESCUDO" -> R.drawable.escudo
        else -> R.drawable.logomine // Fallback to logo if not found
    }

    Image(
        painter = painterResource(id = drawableResId),
        contentDescription = itemId,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
