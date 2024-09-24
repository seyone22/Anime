import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    userPreferences: UserPreferences
) {
    // State to hold the entered username
    var userName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Load the current user name from DataStore
    val currentUserName by userPreferences.userName.collectAsState(initial = "")

    // Use LaunchedEffect to set the username to the current one from DataStore
    LaunchedEffect(currentUserName) {
        userName = currentUserName ?: ""
    }

    // RemoteInput key for capturing input from the wearable input
    val inputTextKey = "input_text"

    // Create the RemoteInput
    val remoteInputs: List<RemoteInput> = listOf(
        RemoteInput.Builder(inputTextKey)
            .setLabel("Enter User ID")
            .wearableExtender {
                setEmojisAllowed(false)
                setInputActionType(EditorInfo.IME_ACTION_DONE)
            }
            .build()
    )

    // Create the launcher to handle the RemoteInput result
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            val results: Bundle? = RemoteInput.getResultsFromIntent(data)
            val newInputText = results?.getCharSequence(inputTextKey)?.toString()
            newInputText?.let {
                userName = it
                // Save the user name to DataStore
                scope.launch {
                    userPreferences.saveUserName(userName)
                }
            }
        }
    }

    // Create the intent for RemoteInput
    val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
    RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)

    // UI Layout
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        state = listState // Pass listState to ScalingLazyColumn
    ) {
        item {
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { launcher.launch(intent) },
                enabled = true,
                label = {
                    Text(
                        text = "Enter User ID",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                secondaryLabel = {
                    Text(
                        text = currentUserName ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                })
        }
    }
}
