package com.majid2851.composeaccesibilityinspector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.majid2851.a11yinspector.AccessibilityInspector
import com.majid2851.composeaccesibilityinspector.ui.theme.ComposeAccesibilityInspectorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeAccesibilityInspectorTheme {
                AccessibilityInspector(enabled = true) {
                    DemoApp()
                }
            }
        }
    }
}

@Composable
private fun DemoApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Broken", "Fixed")

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                    )
                }
            }
            DemoScreen(showFixed = selectedTab == 1)
        }
    }
}

@Composable
private fun DemoScreen(showFixed: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        SectionTitle("Icon buttons")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FakeIconButton(
                color = Color(0xFF6650A4),
                contentDescription = if (showFixed) "Add to favorites" else null,
                sizeDp = if (showFixed) 48 else 24,
            )
            FakeIconButton(
                color = Color(0xFF386A20),
                contentDescription = if (showFixed) "Share" else null,
                sizeDp = if (showFixed) 48 else 24,
            )
        }

        SectionTitle("Decorative image")
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFB3261E))
                .semantics {
                    role = Role.Image
                    if (showFixed) contentDescription = "Company logo"
                },
        )

        SectionTitle("Toolbar actions")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LabeledAction(label = if (showFixed) "Edit" else "More")
            LabeledAction(label = if (showFixed) "Delete" else "More")
        }

        SectionTitle("Body text")
        Text(
            text = "The quick brown fox jumps over the lazy dog.",
            color = if (showFixed) Color(0xFF1C1B1F) else Color(0xFFBFBFBF),
        )

        SectionTitle("Media control")
        FakeIconButton(
            color = Color(0xFF1E88E5),
            contentDescription = if (showFixed) "Play" else "Play button",
            sizeDp = 48,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun FakeIconButton(
    color: Color,
    contentDescription: String?,
    sizeDp: Int,
) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .clickable(onClick = {})
            .semantics {
                if (contentDescription != null) this.contentDescription = contentDescription
            },
    )
}

@Composable
private fun LabeledAction(label: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF625B71))
            .clickable(onClick = {})
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label.take(1), color = Color.White)
    }
}
