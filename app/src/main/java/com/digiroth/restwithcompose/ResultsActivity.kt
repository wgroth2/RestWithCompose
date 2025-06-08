package com.digiroth.restwithcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiroth.restwithcompose.ui.theme.RestWithComposeTheme

@Suppress("SpellCheckingInspection")
class ResultsActivity : ComponentActivity() {

    // VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
    // DEFINE CONSTANTS HERE in a companion object
    // VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
    companion object {
        @Suppress("SpellCheckingInspection")
        const val EXTRA_TICKER_SYMBOL = "com.digiroth.restwithcompose.EXTRA_TICKER_SYMBOL"
        const val EXTRA_EOD_DATA = "com.digiroth.restwithcompose.EXTRA_EOD_DATA"
        const val EXTRA_ERROR_MESSAGE = "com.digiroth.restwithcompose.EXTRA_ERROR_MESSAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Get the Intent and extract data
        val intent = intent // Activity's intent
        val eodDataJson = intent.getStringExtra(EXTRA_EOD_DATA)
        val errorMessage = intent.getStringExtra(EXTRA_ERROR_MESSAGE)
        val tickerSymbol = intent.getStringExtra(EXTRA_TICKER_SYMBOL) ?: "Unknown Symbol" // Example with default

        // Determine what to display based on whether there's an error or data
        val resultsToDisplay: String
        val screenTitle: String

        if (errorMessage != null) {
            screenTitle = "Error"
            resultsToDisplay = "Failed to fetch data for $tickerSymbol:\n$errorMessage"
        } else if (eodDataJson != null) {
            screenTitle = "JSON Results for $tickerSymbol"
            resultsToDisplay = eodDataJson
        } else {
            screenTitle = "No Data"
            resultsToDisplay = "No data received for $tickerSymbol."
        }

        setContent {
            RestWithComposeTheme {
                JsonResultsScreen(screenTitle, resultsToDisplay)
            }
        }
    }
}

@Composable
fun JsonResultsScreen(title: String, jsonData: String) {
    // Dummy long text for scrolling demonstration
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Overall padding for the screen content
        horizontalAlignment = Alignment.CenterHorizontally // Center children horizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall, // Or another appropriate style
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth() // Ensure text is centered across the width
        )

        Spacer(modifier = Modifier.height(8.dp)) // At least 5.dp, using 8.dp for better spacing

        Box(
            modifier = Modifier
                .fillMaxWidth() // Take full available width
                .weight(1f) // Allow the Box to take remaining vertical space
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) // Theme-aware border
                .padding(10.dp) // 10.dp padding inside the border
        ) {
            val scrollState = rememberScrollState()
            Text(
                text = jsonData,
                fontFamily = FontFamily.Monospace, // <-- ADD THIS LINE
                fontSize = 12.sp, // Optional: Adjust font size for readability
                modifier = Modifier
                    .fillMaxSize() // Fill the inner Box
                    .verticalScroll(scrollState), // Make the Text scrollable
                style = MaterialTheme.typography.bodyMedium // Example style
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JsonResultsScreenPreview() {
    val longJsonLikeText = """
        {
          "data": [
            {
              "id": 1,
              "name": "Sample Item Alpha",
              "value": 123.45,
              "description": "This is a longer description for the first sample item, demonstrating how text might wrap and eventually lead to scrolling if enough content is present. We need to ensure that the scrollable area functions correctly."
            },
            {
              "id": 2,
              "name": "Sample Item Beta",
              "value": 67.89,
              "description": "Another item with its own set of details. Each item contributes to the overall content length. This text is purely for demonstration purposes to make the field scrollable."
            }
          ],
          "metadata": {
            "timestamp": "2023-10-27T10:30:00Z",
            "source": "dummy-api",
            "notes": "This is example JSON-like text to fill the scrollable area. In a real application, this would be actual JSON data fetched from a service."
          }
        }
    """.trimIndent().repeat(2) // Repeat to ensure scrolling
    RestWithComposeTheme {
        JsonResultsScreen("JSON Results", longJsonLikeText)
    }
}