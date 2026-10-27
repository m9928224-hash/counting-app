package com.example.counting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

object Palette {
    val bg = Color(0xFFFFF8E1)
    val ink = Color(0xFF3E2723)
    val primary = Color(0xFF1E88E5)
    val good = Color(0xFF43A047)
    val bad = Color(0xFFE53935)
    val neutral = Color(0xFFB0BEC5)
    val cards = listOf(
        Color(0xFFFFCDD2),
        Color(0xFFC8E6C9),
        Color(0xFFBBDEFB),
        Color(0xFFFFE0B2)
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CountingApp() }
    }
}

@Composable
fun CountingApp() {
    MaterialTheme(colorScheme = lightColorScheme(primary = Palette.primary)) {
        // کل برنامه راست‌به‌چپ است
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(modifier = Modifier.fillMaxSize(), color = Palette.bg) {
                Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                    var mode by rememberSaveable { mutableStateOf<Mode?>(null) }
                    BackHandler(enabled = mode != null) { mode = null }

                    val current = mode
                    if (current == null) {
                        MenuScreen(onSelect = { mode = it })
                    } else {
                        QuizScreen(mode = current, onBack = { mode = null })
                    }
                }
            }
        }
    }
}
