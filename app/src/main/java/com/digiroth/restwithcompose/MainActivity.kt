package com.digiroth.restwithcompose

import android.content.Intent // To launch SettingsActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast // For showing simple messages to the user
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // To get context for Intent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.digiroth.restwithcompose.ui.theme.RestWithComposeTheme


// Define a TAG for your logs, usually the class name or a specific feature name
private const val TAG = "MainActivityCompose"

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val marketStackRepository = MarketStackRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestWithComposeTheme {
                val context = LocalContext.current
                // ---- This is where showMenu is declared and initialized ----
                var showMenu by remember { mutableStateOf(false) }
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(id = R.string.app_name)) },
                            actions = {
                                IconButton(onClick = {
                                    showMenu = !showMenu
                                    Log.d(TAG, "IconButton clicked, showMenu is now: $showMenu") // For debugging
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "More options"
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = {
                                        showMenu = false
                                        Log.d(TAG, "Dropdown menu dismissed") // For debugging}
                                    }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(id = R.string.action_settings)) },
                                        onClick = {
                                            showMenu = false
                                            Log.d(TAG, "Settings menu item clicked")
                                            // TODO: Launch SettingsActivity or Navigation to Settings Composable
                                            // Option 1: Launch a new Activity for settings
                                            val intent = Intent(context, SettingsActivity::class.java)
                                            context.startActivity(intent)
                                         }
                                    )
                                    // You can add more DropdownMenuItems here
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    // Use the new StockInputForm here
                    StockInputForm(
                        modifier = Modifier.padding(innerPadding), // Apply innerPadding
                        onTickerEntered = { ticker ->
                            // This is where you'll handle the submitted ticker
                            // For now, let's just log it
                            Log.d("StockTicker", "Submitted ticker: $ticker")
                            // Get Access Key and Base URL from SettingsActivity's companion object methods
                            val accessKey: String = AppSettings.getApiKey(context)

                            if (accessKey.isEmpty()) {
                                Log.e(TAG, "Access Key is missing. Please set it in Settings.")
                                Toast.makeText(context, "Access Key missing. Please configure in Settings.", Toast.LENGTH_LONG).show()
                                // Optionally, navigate to ResultsActivity with an error or directly to Settings
                                // For now, we'll just show a Toast and not proceed with the API call.
                                // You might want to launch ResultsActivity with an error message here:

                                val errorIntent = Intent(context, ResultsActivity::class.java).apply {
                                    putExtra(ResultsActivity.EXTRA_TICKER_SYMBOL, ticker)
                                    putExtra(ResultsActivity.EXTRA_ERROR_MESSAGE, "Access Key is missing. Please set it in Settings.")
                                }
                                context.startActivity(errorIntent)

                                return@StockInputForm // Exit if no key
                            }

                            // --- START: Retrofit API Call Logic ---
                            // Launch a coroutine using lifecycleScope for this Activity
                            lifecycleScope.launch {
                                Log.d(TAG, "Fetching EOD data for $ticker using key '$accessKey' at '$AppSettings.BASE_URL'")

                                // Show a loading indicator (e.g., a Toast or update UI state if using ViewModel)
                                // For simplicity, a Toast here. In a real app, manage a proper loading state.
                                Toast.makeText(context, "Fetching data for $ticker...", Toast.LENGTH_SHORT).show()

                                val result = marketStackRepository.getEndOfDayData(
                                    accessKey = accessKey,
                                    symbols = ticker,
                                    baseUrl = AppSettings.BASE_URL, // Pass the dynamically retrieved base URL
                                    limit = 1 // Fetch the most recent EOD data
                                )

                                result.fold(
                                    onSuccess = { endOfDayResponse ->
                                        val latestData = endOfDayResponse.data.firstOrNull()
                                        if (latestData != null) {
                                            Log.i(TAG, "API Success: Symbol: ${latestData.symbol}, Close: ${latestData.close}, Date: ${latestData.date}")
                                            // val dataString = "Symbol: ${latestData.symbol}\nClose: ${latestData.close}\nVolume: ${latestData.volume}\nDate: ${latestData.date}"
                                            val dataString = latestData.toString()

                                            val intent = Intent(context, ResultsActivity::class.java).apply {
                                                putExtra(ResultsActivity.EXTRA_TICKER_SYMBOL, ticker)
                                                putExtra(ResultsActivity.EXTRA_EOD_DATA, dataString)
                                            }
                                            context.startActivity(intent)
                                        } else {
                                            Log.w(TAG, "API Success but no EOD data found for $ticker in response.")
                                            val intent = Intent(context, ResultsActivity::class.java).apply {
                                                putExtra(ResultsActivity.EXTRA_TICKER_SYMBOL, ticker)
                                                putExtra(ResultsActivity.EXTRA_ERROR_MESSAGE, "No EOD data found for $ticker.")
                                            }
                                            context.startActivity(intent)
                                        }
                                    },
                                    onFailure = { exception ->
                                        Log.e(TAG, "API Error: ${exception.message}", exception)
                                        val intent = Intent(context, ResultsActivity::class.java).apply {
                                            putExtra(ResultsActivity.EXTRA_TICKER_SYMBOL, ticker)
                                            putExtra(ResultsActivity.EXTRA_ERROR_MESSAGE, "API Error: ${exception.localizedMessage}")
                                        }
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Activity started")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Activity resumed")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Activity paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Activity stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Activity being destroyed")
    }
}

//
// logging
//

// StockInputForm Composable (as defined above)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StockInputForm(
    modifier: Modifier = Modifier,
    onTickerEntered: (String) -> Unit
) {
    var tickerText by remember { mutableStateOf("GOOG") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val submitAction = {
        if (tickerText.isNotBlank() && tickerText.length <= 7) {
            onTickerEntered(tickerText.uppercase())
            keyboardController?.hide()
        }
    }

    Column(
        modifier = modifier.padding(16.dp), // Additional padding for the form's content
        horizontalAlignment = Alignment.CenterHorizontally,
        // verticalArrangement = Arrangement.Center // You might want to remove this if you want the form at the top
    ) {
        OutlinedTextField(
            value = tickerText,
            onValueChange = { newText ->
                if (newText.length <= 7) {
                    tickerText = newText.uppercase()
                }
            },
            label = { Text("Ticker (1-7 characters)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { submitAction() }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { submitAction() },
            modifier = Modifier.align(Alignment.End),
            enabled = tickerText.isNotBlank() && tickerText.length <= 7
        ) {
            Text("Submit")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() { // Keep or rename your original preview if needed
    RestWithComposeTheme {
        Scaffold { innerPadding ->
            StockInputForm(
                modifier = Modifier.padding(innerPadding),
                onTickerEntered = { ticker -> println("Preview Ticker: $ticker") }
            )
        }
    }
}
