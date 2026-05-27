package com.example.movablecontentbugreproducer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.mikepenz.markdown.m3.Markdown
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

// Navigation keys
@Serializable
data object ScreenAKey : NavKey

@Serializable
data object ScreenBKey : NavKey

val AppTypography = Typography(
    displayMedium = TextStyle(
        fontSize = 80.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 88.sp,
    ),
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = AppTypography,
        content = content,
    )
}

@Composable
fun App() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val backStack = rememberNavBackStack(ScreenAKey)

            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<ScreenAKey> {
                        ScreenA(onNavigateToB = { backStack.add(ScreenBKey) })
                    }
                    entry<ScreenBKey> {
                        ScreenB(onClose = { backStack.removeLastOrNull() })
                    }
                },
            )
        }
    }
}

@Composable
fun ScreenA(onNavigateToB: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Large Title Text",
            style = MaterialTheme.typography.displayMedium,
        )
        Button(onClick = onNavigateToB) {
            Text("Go to Screen B")
        }
    }
}

@Composable
fun ScreenB(onClose: () -> Unit) {
    val markdownContent = buildString {
        repeat(50) { index ->
            appendLine("**Item $index** — Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
            appendLine()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Markdown(content = markdownContent)
        Button(onClick = onClose) {
            Text("Close")
        }
    }
}
