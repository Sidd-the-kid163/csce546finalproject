package com.example.acccomparison

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import com.example.acccomparison.ui.GameScreen
import com.example.acccomparison.ui.GameStatus
import com.example.acccomparison.ui.GameUiState
import com.example.acccomparison.ui.GameViewModel
import com.example.acccomparison.ui.theme.AccComparisonTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()
    val messageLiveData = MutableLiveData(GameUiState(false, 0, 0, 0, 0))
    private val createFileLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.writeToFile(uri)  // Give context so ViewModel can write
        }
    }
    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        getDataFromFile.launch(intent)
    }
    private var getDataFromFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                viewModel.readToFile(uri!!, contentResolver)
                //Toast.makeText(this@MainActivity, fileContents, Toast.LENGTH_SHORT).show()
            }
        }
    private fun getRandomFileName(): String {
        return Calendar.getInstance().timeInMillis.toString() + ".json"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
            // The lifecycle is at least STARTED (STARTED or RESUMED)
            Log.d("LIFECYCLE", "STARTED")
            viewModel.requestFileCreation.observe(this) {
                Log.d("DEBUG", "Observer fired! Value: $it")
            }
            viewModel.requestFileCreation.observe(this) { shouldCreate ->
                Log.e("function", "function being called")
                if (shouldCreate) {
                    createFileLauncher.launch(getRandomFileName())
                }
                viewModel.resetFileCreationTrigger()
            }
            viewModel.acc.observe(this) { shouldCreate ->
                Log.e("function", "acc being called")
                messageLiveData.postValue(shouldCreate)
            }

        setContent {
            val context = LocalContext.current
            val application = context.applicationContext as Application
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            AccComparisonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val message by messageLiveData.observeAsState(GameUiState(false, 0, 0, 0, 0))
                    GameScreen(GameViewModel(application), onButtonClick1 = {viewModel.onResume()}, onButtonClick2 = {openFileManager()}, onButtonClick3 = {viewModel.onResume()}, message.accStatus, message.excellents, message.goods, message.oks, message.totals, message.color)
                }
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AccComparisonTheme {
        GameScreen(context)
    }
}
 */