package com.example.gochatapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx. compose. foundation. layout. safeDrawing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    floatingActionButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable (innerPadding: androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        // 👇 Safe insets rakho taki topBar notch / status bar se neeche aaye
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets.safeDrawing,
        topBar = {
            if (topBar != null) {
                // Direct padding lagao
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.statusBarsPadding()
                ) { topBar() }
            }
        },
        bottomBar = { bottomBar?.invoke() },
        floatingActionButton = { floatingActionButton?.invoke() },
        containerColor = containerColor,
        content = content
    )
}
